package code.model.dto.booking;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class UserBookingRequestDTO extends BaseRequestBooking {
    // userId sẽ được lấy từ session khi user đã đăng nhập
}
