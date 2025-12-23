package code.model.dto.booking;

import code.model.entity.rooms.RoomEntity;
import code.model.entity.users.UserEntity;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingResponseDTO {
    private String bookingId;
    private LocalDateTime checkInTime;
    private LocalDateTime checkOutTime;
    private String status; // Chỗ này thì status là PENDING, CONFIRM, CANCELLED, COMPLETED
    private BigDecimal totalPrice;
    private String userId;
    private String roomId;
}
