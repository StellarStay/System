package code.services.payments;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
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
            throw new ResourceNotFoundException("Temp Booking not found");
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
        // Lấy orderId chính là tempBookingId
        String tempBookingId = momoResponse.getOrderId();
        int resultCode = momoResponse.getResultCode();

        // Nếu thanh toán thất bại, return false
        if (resultCode != 0) {
            throw new BadRequestException("Payment failed !!!");
        }

        // Lấy thông tin booking tạm từ Redis
        TempBookingBeforePaymentDTO tempBookingBeforePaymentDTO = tempBookingService.get(tempBookingId);
        if (tempBookingBeforePaymentDTO == null) {
            throw new ResourceNotFoundException("Temp Booking not found");
        }

        // Lấy paymentMethodId từ TempBooking (đã được set trước đó)
        String paymentMethodId = tempBookingBeforePaymentDTO.getPaymentMethodId();
        if (paymentMethodId == null || paymentMethodId.isEmpty()) {
            throw new ResourceNotFoundException("Payment method not found in temp booking in Redis");
        }

        // Tạo booking chính thức từ temp booking
        BookingEntity bookingEntity = bookingService.insertBookingFromTemp(tempBookingBeforePaymentDTO.getTempBookingId());

        // Tạo booking contact (cho guest và cả user)
        if (tempBookingBeforePaymentDTO.getBookingContactRequestDTO() != null) {
            bookingContactService.insertBookingContact(bookingEntity, tempBookingBeforePaymentDTO.getBookingContactRequestDTO());
        }

        // Lấy payment method
        PaymentMethodEntity paymentMethod = paymentMethodService.getPaymentMethodByPaymentMethodId(paymentMethodId);
        if (paymentMethod == null) {
            throw new ResourceNotFoundException("Payment method not found in database");
        }

        // Tạo payment record
        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentId(generatePaymentId());
        paymentEntity.setBooking(bookingEntity);
        paymentEntity.setPaymentMethod(paymentMethod);
        paymentEntity.setPaymentStatus("SUCCESS");
        paymentEntity.setPaidAt(LocalDateTime.now());
        paymentEntity.setTotalPrice(tempBookingBeforePaymentDTO.getTotalPrice());
        paymentRepository.save(paymentEntity);

        // Xóa temp booking khỏi Redis
        tempBookingService.delete(tempBookingId);
        return true;
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
        if (list.isEmpty()) {
            throw new ResourceNotFoundException("Payment List not found");
        }
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
