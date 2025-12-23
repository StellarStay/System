package code.model.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentRequestDTO {
//    private BigDecimal totalPrice;
    private String tempBookingId;
    private String paymentMethodId;

}
