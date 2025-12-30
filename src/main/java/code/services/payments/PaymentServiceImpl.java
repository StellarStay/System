package code.services.payments;

import code.model.dto.booking.TempBookingBeforePaymentDTO;
import code.model.dto.momo.MomoRequestDTO;
import code.model.dto.momo.MomoResponseDTO;
import code.model.dto.payments.PaymentRequestDTO;
import code.model.dto.payments.PaymentResponseDTO;
import code.model.entity.booking.BookingEntity;
import code.model.entity.payments.PaymentEntity;
import code.model.entity.payments.PaymentMethodEntity;
import code.repository.payments.PaymentRepository;
import code.services.booking.BookingService;
import code.services.booking.TempBookingService;
import code.services.booking_contact.BookingContactService;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class PaymentServiceImpl implements  PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private BookingService bookingService;
    @Autowired
    private TempBookingService tempBookingService;
    @Autowired
    private BookingContactService bookingContactService;

    @Value("${momo.end-point}")
    private String momoEndPoint;
    @Value("${momo.access-key}")
    private String momoAccessKey;
    @Value("${momo.secret-key}")
    private String momoSecretKey;
    @Value("${momo.ipn-url}")
    private String momoIpnUrl;
    @Value("${momo.return-url}")
    private String momoReturnUrl;
    @Value("${momo.request-type}")
    private String momoRequestType;
    @Value("${momo.partner-code}")
    private String momoPartnerCode;


    int max_length_payment_id = 8;
    private String generatePaymentId() {
        String paymentId;
        do{
            paymentId = RandomId.generateRoomId(max_length_payment_id);
        }while (paymentRepository.existsById(paymentId));
        return paymentId;
    }

    @Override
    public MomoResponseDTO insertPayment(PaymentRequestDTO paymentRequestDTO) {
        // Lấy thông tin đơn hàng từ Redis
        TempBookingBeforePaymentDTO tempBooking = tempBookingService.get(paymentRequestDTO.getTempBookingId());
        if (tempBooking == null) {
            throw new RuntimeException("Temp Booking not found");
        }

        // Tạo yêu cầu thanh toán tới momo
        MomoRequestDTO momoRequestDTO = MomoRequestDTO.builder()
                .partnerCode(momoPartnerCode)
                .requestType(momoRequestType) // "captureWallet"
                .ipnUrl(momoIpnUrl)
                .redirectUrl(momoReturnUrl)
                .orderId(tempBooking.getTempBookingId())
                .amount(tempBooking.getTotalPrice().longValue())
                .orderInfo("Thanh toan don hang " + tempBooking.getTempBookingId())
                .requestId("REQ_" + tempBooking.getTempBookingId())
                .extraData("")  // Empty string if no extra data
                .lang("vi")  // Vietnamese language
                .build();

        String signature = generateSignature(momoRequestDTO);
        momoRequestDTO.setSignature(signature);

        // Gửi yêu cầu thanh toán tới MOMO
        RestTemplate template = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Tạo một HTTP entity với body và headers có ý nghĩa để gửi yêu cầu HTTP đến MOMO
        HttpEntity<MomoRequestDTO> request = new HttpEntity<>(momoRequestDTO, headers);
        // Gửi yêu cầu và nhận phản hồi từ MOMO (dùng POST method)
        ResponseEntity<MomoResponseDTO> response = template.exchange(momoEndPoint, HttpMethod.POST, request, MomoResponseDTO.class);

        return response.getBody();
    }

    @Override
    public boolean ipnHandler(MomoResponseDTO momoResponse) {
        try {
            // Lấy orderId chính là tempBookingId
            String tempBookingId = momoResponse.getOrderId();
            int resultCode = momoResponse.getResultCode();

            System.out.println("\n=== IPN Handler Debug ===");
            System.out.println("TempBookingId: " + tempBookingId);
            System.out.println("ResultCode: " + resultCode);
            System.out.println("Amount: " + momoResponse.getAmount());
            System.out.println("Message: " + momoResponse.getMessage());

            // Nếu thanh toán thất bại, return false
            if (resultCode != 0) {
                System.out.println("❌ Payment failed with resultCode: " + resultCode);
                return false;
            }

            // Lấy thông tin booking tạm từ Redis
            System.out.println("📝 Fetching TempBooking from Redis...");
            TempBookingBeforePaymentDTO tempBookingBeforePaymentDTO = tempBookingService.get(tempBookingId);

            if (tempBookingBeforePaymentDTO == null) {
                System.out.println("❌ ERROR: Temp Booking not found for id: " + tempBookingId);
                System.out.println("💡 Possible reasons:");
                System.out.println("  1. TempBooking expired (TTL = 10 minutes)");
                System.out.println("  2. TempBooking was already processed and deleted");
                System.out.println("  3. Wrong tempBookingId");
                throw new RuntimeException("Temp Booking not found for id: " + tempBookingId);
            }

            System.out.println("✅ TempBooking found!");
            System.out.println("  - TempBookingId: " + tempBookingBeforePaymentDTO.getTempBookingId());
            System.out.println("  - RoomId: " + tempBookingBeforePaymentDTO.getRoomId());
            System.out.println("  - UserId: " + (tempBookingBeforePaymentDTO.getUserId() != null ? tempBookingBeforePaymentDTO.getUserId() : "null (guest)"));
            System.out.println("  - TotalPrice: " + tempBookingBeforePaymentDTO.getTotalPrice());
            System.out.println("  - PaymentMethodId: " + tempBookingBeforePaymentDTO.getPaymentMethodId());
            System.out.println("  - BookingContact: " + tempBookingBeforePaymentDTO.getBookingContactRequestDTO());

            // Lấy paymentMethodId từ TempBooking (đã được set trước đó)
            String paymentMethodId = tempBookingBeforePaymentDTO.getPaymentMethodId();
            if (paymentMethodId == null || paymentMethodId.isEmpty()) {
                System.out.println("❌ ERROR: Payment method not selected");
                throw new RuntimeException("Payment method not selected. Please call /api/payment_method/choose_method first");
            }

            // Tạo booking chính thức từ temp booking
            System.out.println("📝 Creating booking from temp booking...");
            BookingEntity bookingEntity = bookingService.insertBookingFromTemp(tempBookingBeforePaymentDTO.getTempBookingId());
            System.out.println("✅ Booking created: " + bookingEntity.getBookingId());

            // Tạo booking contact nếu có (cho guest)
            if (tempBookingBeforePaymentDTO.getBookingContactRequestDTO() != null) {
                System.out.println("📝 Creating booking contact for guest...");
                bookingContactService.insertBookingContact(bookingEntity, tempBookingBeforePaymentDTO.getBookingContactRequestDTO());
                System.out.println("✅ Booking contact created");
            }

            // Lấy payment method
            System.out.println("📝 Fetching payment method: " + paymentMethodId);
            PaymentMethodEntity paymentMethod = paymentMethodService.getPaymentMethodByPaymentMethodId(paymentMethodId);
            if (paymentMethod == null) {
                System.out.println("❌ ERROR: Payment method not found: " + paymentMethodId);
                throw new RuntimeException("Payment method not found");
            }
            System.out.println("✅ Payment method found: " + paymentMethod.getPaymentMethodName());

            // Tạo payment record
            System.out.println("📝 Creating payment record...");
            PaymentEntity paymentEntity = new PaymentEntity();
            paymentEntity.setPaymentId(generatePaymentId());
            paymentEntity.setBooking(bookingEntity);
            paymentEntity.setPaymentMethod(paymentMethod);
            paymentEntity.setPaymentStatus("SUCCESS");
            paymentEntity.setPaidAt(LocalDateTime.now());
            paymentEntity.setTotalPrice(tempBookingBeforePaymentDTO.getTotalPrice());
            paymentRepository.save(paymentEntity);
            System.out.println("✅ Payment record saved: " + paymentEntity.getPaymentId());

            // Xóa temp booking khỏi Redis
            System.out.println("📝 Deleting temp booking from Redis...");
            tempBookingService.delete(tempBookingId);
            System.out.println("✅ IPN Handler completed successfully!\n");
            return true;

        } catch (Exception e) {
            System.out.println("❌ ERROR in IPN Handler: " + e.getMessage());
            e.printStackTrace();
            throw e;
        }
    }

    private String generateSignature(MomoRequestDTO momoRequestDTO) {
        try {
            // Build raw signature string according to MoMo API specification
            String rawSignature = "accessKey=" + momoAccessKey +
                    "&amount=" + momoRequestDTO.getAmount() +
                    "&extraData=" + momoRequestDTO.getExtraData() +
                    "&ipnUrl=" + momoRequestDTO.getIpnUrl() +
                    "&orderId=" + momoRequestDTO.getOrderId() +
                    "&orderInfo=" + momoRequestDTO.getOrderInfo() +
                    "&partnerCode=" + momoRequestDTO.getPartnerCode() +
                    "&redirectUrl=" + momoRequestDTO.getRedirectUrl() +
                    "&requestId=" + momoRequestDTO.getRequestId() +
                    "&requestType=" + momoRequestDTO.getRequestType();

            // Generate HMAC SHA256 signature
            javax.crypto.Mac hmacSHA256 = javax.crypto.Mac.getInstance("HmacSHA256");
            javax.crypto.spec.SecretKeySpec secretKeySpec = new javax.crypto.spec.SecretKeySpec(
                    momoSecretKey.getBytes(java.nio.charset.StandardCharsets.UTF_8),
                    "HmacSHA256"
            );
            hmacSHA256.init(secretKeySpec);
            byte[] hash = hmacSHA256.doFinal(rawSignature.getBytes(java.nio.charset.StandardCharsets.UTF_8));

            // Convert to hex string
            StringBuilder hexString = new StringBuilder();
            for (byte b : hash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) hexString.append('0');
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (Exception e) {
            throw new RuntimeException("Error generating MoMo signature", e);
        }
    }

//    @Override
//    public boolean updatePayment(String paymentId, PaymentRequestDTO paymentRequestDTO) {
//        PaymentEntity getPayment = paymentRepository.findById(paymentId).orElse(null);
//        if(getPayment == null) {
//            return false;
//        }
//        BookingEntity bookingForPayment = bookingService.getBookingById(paymentRequestDTO.getBookingId());
//        PaymentMethodEntity paymentMethod = paymentMethodService.getPaymentMethodByPaymentMethodId(paymentRequestDTO.getPaymentMethodId());
//        if(bookingForPayment == null || paymentMethod == null){
//            return false;
//        }
//        getPayment.setBooking(bookingForPayment);
//        getPayment.setPaymentMethod(paymentMethod);
//        getPayment.setTotalPrice(bookingForPayment.getTotalPrice());
//        paymentRepository.save(getPayment);
//        return true;
//    }

    @Override
    public PaymentEntity getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    @Override
    public List<PaymentResponseDTO> getAllPayment() {
        List<PaymentEntity> list = paymentRepository.findAll();
        List<PaymentResponseDTO> paymentResponseDTOList = new ArrayList<>();
        for (PaymentEntity paymentEntity : list) {
           PaymentResponseDTO paymentResponseDTO = new PaymentResponseDTO();
           paymentResponseDTO.setPaymentMethodId(paymentEntity.getPaymentId());
           paymentResponseDTO.setBookingId(paymentEntity.getBooking().getBookingId());
           paymentResponseDTO.setTotalPrice(paymentEntity.getTotalPrice());
           paymentResponseDTOList.add(paymentResponseDTO);
        }
        return paymentResponseDTOList;
    }
    // Ở phần payment, payment chỉ được tạo ra khi thanh toán thành công và đồng thời sẽ cập nhật trạng thái booking từ PENDING sang CONFIRMED
}
