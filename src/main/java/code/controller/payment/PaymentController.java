package code.controller.payment;

import code.model.dto.momo.MomoResponseDTO;
import code.model.dto.payments.PaymentRequestDTO;
import code.services.payments.PaymentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/payment")
public class PaymentController {
    @Autowired
    private PaymentService paymentService;

    @PostMapping("/create_payment")
    public ResponseEntity<MomoResponseDTO> createPayment(@RequestBody PaymentRequestDTO paymentRequestDTO) {
        MomoResponseDTO momoResponseDTO= paymentService.insertPayment(paymentRequestDTO);
        if (momoResponseDTO != null) {
            return new ResponseEntity<>(momoResponseDTO, HttpStatus.OK);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }

    @PostMapping("/ipn-handler")
    public ResponseEntity<String> handleIPN(@RequestBody MomoResponseDTO momoResponse) {
        boolean isProcessed = paymentService.ipnHandler(momoResponse);
        if (isProcessed) {
            return ResponseEntity.ok("IPN processed successfully");
        } else {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to process IPN");
        }
    }
}
