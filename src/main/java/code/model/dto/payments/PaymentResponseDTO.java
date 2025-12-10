package code.model.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentResponseDTO {
    private BigDecimal totalPrice;
    private String bookingId;
    private String paymentMethodId;
}
