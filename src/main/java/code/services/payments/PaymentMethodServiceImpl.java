package code.services.payments;

import code.model.dto.payments.PaymentMethodRequestDTO;
import code.model.entity.payments.PaymentMethodEntity;
import code.repository.payments.PaymentMethodRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements  PaymentMethodService {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    int max_length_payment_method_id = 8;
    private String generatePaymentMethodId() {
        String paymentMethodId;
        do{
            paymentMethodId = RandomId.generateRoomId(max_length_payment_method_id);
        }while (paymentMethodRepository.existsById(paymentMethodId));
        return paymentMethodId;
    }

    // Chức năng thêm phương thức thanh toán dành cho admin
    @Override
    public boolean addPaymentMethod(PaymentMethodRequestDTO paymentMethodRequestDTO) {
        PaymentMethodEntity paymentMethodEntity = new PaymentMethodEntity();
        paymentMethodEntity.setPaymentMethodId(generatePaymentMethodId());
        paymentMethodEntity.setPaymentMethodName(paymentMethodRequestDTO.getPaymentMethodName());
        paymentMethodEntity.setDescription(paymentMethodRequestDTO.getPaymentMethodDescription());
        paymentMethodRepository.save(paymentMethodEntity);
        return true;
    }

//    @Override
//    public boolean updatePaymentMethod(String paymentId, String paymentMethodId) {
//        return false;
//    }

    @Override
    public boolean deletePaymentMethod(String paymentMethodId) {
        PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(paymentMethodId).orElse(null);
        if (paymentMethodEntity == null) {
            return false;
        }
        paymentMethodRepository.delete(paymentMethodEntity);
        return true;
    }

    @Override
    public PaymentMethodEntity getPaymentMethodByPaymentMethodId(String paymentMethodId) {
        return paymentMethodRepository.findById(paymentMethodId).orElse(null);
    }

    @Override
    public List<PaymentMethodEntity> getAllPaymentMethods() {
        return paymentMethodRepository.findAll();
    }
}
