package code.model.dto.booking;

import code.model.dto.booking_contact.BookingContactRequestDTO;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class GuestBookingRequestDTO extends BaseRequestBooking {
    private BookingContactRequestDTO bookingContactRequestDTO;
}
