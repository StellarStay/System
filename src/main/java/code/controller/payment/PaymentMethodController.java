package code.controller.payment;

import code.model.dto.payments.ChoosePaymentMethodDTO;
import code.model.dto.payments.PaymentMethodRequestDTO;
import code.services.payments.PaymentMethodService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment_method")
public class PaymentMethodController {

    @Autowired
    private PaymentMethodService paymentMethodService;

    @PostMapping("/choose_method")
    public ResponseEntity<String> choosePaymentMethod(@RequestBody ChoosePaymentMethodDTO choosePaymentMethodDTO) {

        String tempBookingId = choosePaymentMethodDTO.getTempBookingId();
        String paymentMethodId = choosePaymentMethodDTO.getPaymentMethodId();

        boolean checkChooseMethod = paymentMethodService.updatePaymentMethodId(tempBookingId, paymentMethodId);
        if (!checkChooseMethod) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok("Payment method selected successfully");
    }
}
