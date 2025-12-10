package code.services.payments;

import code.model.dto.payments.PaymentRequestDTO;
import code.model.entity.booking.BookingEntity;
import code.model.entity.payments.PaymentEntity;
import code.model.entity.payments.PaymentMethodEntity;
import code.repository.payments.PaymentRepository;
import code.services.booking.BookingService;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class PaymentServiceImpl implements  PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;
    @Autowired
    private PaymentMethodService paymentMethodService;
    @Autowired
    private BookingService bookingService;

    int max_length_payment_id = 8;
    private String generatePaymentId() {
        String paymentId;
        do{
            paymentId = RandomId.generateRoomId(max_length_payment_id);
        }while (paymentRepository.existsById(paymentId));
        return paymentId;
    }

    @Override
    public boolean insertPayment(PaymentRequestDTO paymentRequestDTO) {
        PaymentEntity paymentEntity = new PaymentEntity();
        BookingEntity bookingForPayment = bookingService.getBookingById(paymentRequestDTO.getBookingId());
        PaymentMethodEntity paymentMethod = paymentMethodService.getPaymentMethodByPaymentMethodId(paymentRequestDTO.getPaymentMethodId());
        if(bookingForPayment == null || paymentMethod == null){
            return false;
        }

        paymentEntity.setPaymentId(generatePaymentId());
        paymentEntity.setBooking(bookingForPayment);
        paymentEntity.setPaymentMethod(paymentMethod);
        paymentEntity.setTotalPrice(bookingForPayment.getTotalPrice());
        paymentEntity.setPaymentStatus("SUCCESS");
        paymentEntity.setPaidAt(LocalDateTime.now());
        paymentRepository.save(paymentEntity);
        return true;
    }

    @Override
    public boolean updatePayment(String paymentId, PaymentRequestDTO paymentRequestDTO) {
        PaymentEntity getPayment = paymentRepository.findById(paymentId).orElse(null);
        if(getPayment == null) {
            return false;
        }
        BookingEntity bookingForPayment = bookingService.getBookingById(paymentRequestDTO.getBookingId());
        PaymentMethodEntity paymentMethod = paymentMethodService.getPaymentMethodByPaymentMethodId(paymentRequestDTO.getPaymentMethodId());
        if(bookingForPayment == null || paymentMethod == null){
            return false;
        }
        getPayment.setBooking(bookingForPayment);
        getPayment.setPaymentMethod(paymentMethod);
        getPayment.setTotalPrice(bookingForPayment.getTotalPrice());
        paymentRepository.save(getPayment);
        return true;
    }

    @Override
    public PaymentEntity getPaymentById(String paymentId) {
        return paymentRepository.findById(paymentId).orElse(null);
    }

    @Override
    public List<PaymentEntity> getAllPayment() {
        return paymentRepository.findAll();
    }
    // Ở phần payment, payment chỉ được tạo ra khi thanh toán thành công và đồng thời sẽ cập nhật trạng thái booking từ PENDING sang CONFIRMED
}
