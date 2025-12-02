package code.model.entity.payments;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "payment_methods")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class PaymentMethodEntity {
    @Id
    @Column(name = "payment_method_id", nullable = false)
    private String paymentMethodId;
    @Column(name = "payment_method_name", nullable = false)
    private String paymentMethodName;
    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "paymentMethod")
    private List<PaymentEntity> payments;
}
