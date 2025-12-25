package code.model.dto.booking;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TempBookingBeforePaymentDTO {
    private String tempBookingId;
    private String roomId;
    private String userId; // Có thể là userId của user đã đăng nhập hoặc null nếu là guest
    private LocalDateTime planCheckInTime;
    private LocalDateTime planCheckOutTime;
    private BigDecimal totalPrice;

}
