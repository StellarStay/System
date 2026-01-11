package code.services.payments;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.model.dto.booking.TempBookingBeforePaymentDTO;
import code.model.dto.payments.PaymentMethodRequestDTO;
import code.model.entity.payments.PaymentMethodEntity;
import code.repository.payments.PaymentMethodRepository;
import code.services.booking.TempBookingService;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PaymentMethodServiceImpl implements  PaymentMethodService {
    @Autowired
    private PaymentMethodRepository paymentMethodRepository;
    @Autowired
    private TempBookingService tempBookingService;


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
        if (paymentMethodRequestDTO == null) {
            throw new BadRequestException("Payment Method Request is null");
        }
        PaymentMethodEntity paymentMethodEntity = new PaymentMethodEntity();
        paymentMethodEntity.setPaymentMethodId(generatePaymentMethodId());
        paymentMethodEntity.setPaymentMethodName(paymentMethodRequestDTO.getPaymentMethodName());
        paymentMethodEntity.setDescription(paymentMethodRequestDTO.getPaymentMethodDescription());
        paymentMethodRepository.save(paymentMethodEntity);
        return true;
    }

    @Override
    public boolean updatePaymentMethodId(String tempBookingId, String paymentMethodId) {
        if (tempBookingId == null || paymentMethodId == null) {
            throw new BadRequestException("Temp booking Id or Payment method Id is null");
        }
        TempBookingBeforePaymentDTO tempBookingBeforePaymentDTO = tempBookingService.get(tempBookingId);
        if (tempBookingBeforePaymentDTO == null) {
            throw new ResourceNotFoundException("Temp booking not found");
        }

        PaymentMethodEntity paymentMethod = paymentMethodRepository.findById(paymentMethodId).orElse(null);
        if (paymentMethod == null) {
            throw new ResourceNotFoundException("Payment method not found");
        }
        tempBookingBeforePaymentDTO.setPaymentMethodId(paymentMethod.getPaymentMethodId());
        tempBookingService.save(tempBookingBeforePaymentDTO);
        return true;
    }

    @Override
    public boolean deletePaymentMethod(String paymentMethodId) {
        if (paymentMethodId == null) {
            throw new BadRequestException("Payment method Id is null");
        }
        PaymentMethodEntity paymentMethodEntity = paymentMethodRepository.findById(paymentMethodId).orElse(null);
        if (paymentMethodEntity == null) {
            throw new ResourceNotFoundException("Payment method not found");
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
