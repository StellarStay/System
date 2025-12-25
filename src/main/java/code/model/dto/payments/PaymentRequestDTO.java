package code.model.dto.payments;

import code.model.dto.booking_contact.BookingContactRequestDTO;
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

    private BookingContactRequestDTO bookingContactRequest;

}
