package code.model.entity.users;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Entity
@Table(name = "roles")
@Data
@AllArgsConstructor
@NoArgsConstructor
public class RoleEntity {
    @Id
    @Column(name = "role_id", nullable = false)
    private String roleId;
    @Column(name = "role_name", nullable = false, unique = true)
    private String roleName;
    @Column(name = "description", nullable = true)
    private String description;

    @OneToMany(mappedBy = "role")
    private List<UserEntity> users;
}
