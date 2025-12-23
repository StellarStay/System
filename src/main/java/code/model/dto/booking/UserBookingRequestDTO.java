package code.model.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@EqualsAndHashCode(callSuper = true)
public abstract class UserBookingRequestDTO extends BaseRequestBooking {
    // userId sẽ được lấy từ session khi user đã đăng nhập
}
