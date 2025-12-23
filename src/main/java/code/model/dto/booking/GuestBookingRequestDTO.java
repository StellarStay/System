package code.model.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class GuestBookingRequestDTO extends BaseRequestBooking {
}
