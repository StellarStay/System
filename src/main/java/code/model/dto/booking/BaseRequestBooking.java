package code.model.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BaseRequestBooking {
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String roomId;
}
