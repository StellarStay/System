package code.model.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingRequestDTO {
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String userId;
    private String roomId;
    private String status;
}
