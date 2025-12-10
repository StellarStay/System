package code.model.dto.payments;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodResponseDTO {
    private String paymentMethodName;
    private String paymentMethodDescription;
}
