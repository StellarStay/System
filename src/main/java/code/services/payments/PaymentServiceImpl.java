package code.services.payments;

import code.model.dto.booking.TempBookingBeforePaymentDTO;
import code.model.dto.payments.PaymentRequestDTO;
import code.model.dto.payments.PaymentResponseDTO;
import code.model.entity.booking.BookingEntity;
import code.model.entity.booking_contact.BookingContactEntity;
import code.model.entity.payments.PaymentEntity;
import code.model.entity.payments.PaymentMethodEntity;
import code.repository.payments.PaymentRepository;
import code.services.booking.BookingService;
import code.services.booking.TempBookingService;
import code.services.booking_contact.BookingContactService;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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

    int max_length_payment_id = 8;
    private String generatePaymentId() {
        String paymentId;
        do{
            paymentId = RandomId.generateRoomId(max_length_payment_id);
        }while (paymentRepository.existsById(paymentId));
        return paymentId;
    }

    @Override
    public boolean createPayment(PaymentRequestDTO paymentRequestDTO) {
        PaymentMethodEntity paymentMethod = paymentMethodService.getPaymentMethodByPaymentMethodId(paymentRequestDTO.getPaymentMethodId());
        if (paymentMethod == null) {
            return false;
        }

        BookingEntity bookingEntity = bookingService.insertBookingFromTemp(paymentRequestDTO.getTempBookingId());
        if (bookingEntity == null) {
            return  false;
        }
        BookingContactEntity bookingContactEntity = bookingContactService.insertBookingContact(bookingEntity, paymentRequestDTO.getBookingContactRequest());
        if (bookingContactEntity == null) {
            return false;
        }

        PaymentEntity paymentEntity = new PaymentEntity();
        paymentEntity.setPaymentId(generatePaymentId());
        paymentEntity.setBooking(bookingEntity);
        paymentEntity.setPaymentMethod(paymentMethod);
        paymentEntity.setTotalPrice(bookingEntity.getTotalPrice());
        paymentEntity.setPaymentStatus("SUCCESS");
        paymentEntity.setPaidAt(LocalDateTime.now());
        paymentRepository.save(paymentEntity);

        tempBookingService.delete(paymentRequestDTO.getTempBookingId());
        return true;
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
