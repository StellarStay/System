package code.model.entity.booking;

import code.model.entity.payments.PaymentEntity;
import code.model.entity.rooms.RoomEntity;
import code.model.entity.users.UserEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bookings")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class BookingEntity {
    @Id
    @Column(name = "booking_id", nullable = false)
    private String bookingId;
    @Column(name = "check_in_time", nullable = true)
    private LocalDateTime checkInTime;
    @Column(name = "check_out_time", nullable = true)
    private LocalDateTime checkOutTime;
    @Column(name = "status", nullable = false)
    private String status; // Chỗ này thì status là PENDING, CONFIRM, CANCELLED, COMPLETED
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @ManyToOne
    @JoinColumn(name = "user_booking_id", nullable = true)
    private UserEntity user;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private RoomEntity room;

    @OneToOne(mappedBy = "booking")
    private PaymentEntity payment;
}
