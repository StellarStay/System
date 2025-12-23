package code.services.payments;

import code.model.dto.payments.PaymentRequestDTO;
import code.model.dto.payments.PaymentResponseDTO;
import code.model.entity.payments.PaymentEntity;

import java.util.List;

public interface PaymentService {
    boolean insertPayment(PaymentRequestDTO paymentRequestDTO);
//    boolean updatePayment(String paymentId, PaymentRequestDTO paymentRequestDTO);
    PaymentEntity getPaymentById(String paymentId);
    List<PaymentResponseDTO> getAllPayment();
}
