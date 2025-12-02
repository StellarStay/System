package code.model.entity.payments;

import code.model.entity.booking.BookingEntity;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "payments")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentEntity {
    @Id
    @Column(name = "payment_id", nullable = false)
    private String paymentId;
    @Column(name = "payment_status", nullable = false)
    private String paymentStatus; // chỗ này thì paymentStatus là Pending, Success, Failed
    @Column(name = "paid_at", nullable = true)
    private LocalDateTime paidAt;
    @Column(name = "total_price", nullable = false)
    private BigDecimal totalPrice;

    @OneToOne
    @JoinColumn(name = "booking_id", nullable = false)
    private BookingEntity booking;

    @ManyToOne
    @JoinColumn(name = "payment_method_id", nullable = false)
    private PaymentMethodEntity paymentMethod;
}
