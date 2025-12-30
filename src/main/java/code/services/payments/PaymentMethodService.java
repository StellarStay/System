package code.services.payments;

import code.model.dto.payments.PaymentMethodRequestDTO;
import code.model.entity.payments.PaymentMethodEntity;

import java.util.List;

public interface PaymentMethodService {
    boolean addPaymentMethod(PaymentMethodRequestDTO paymentMethodRequestDTO);
    boolean updatePaymentMethodId(String tempBookingId, String paymentMethodId);
    boolean deletePaymentMethod(String paymentMethodId);
    PaymentMethodEntity getPaymentMethodByPaymentMethodId(String paymentMethodId);
    List<PaymentMethodEntity> getAllPaymentMethods();
}
