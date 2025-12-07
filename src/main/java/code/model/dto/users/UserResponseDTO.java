package code.model.dto.users;

import code.model.entity.users.RoleEntity;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponseDTO {
    private String userId;
    private String idCard;
    private String firstName;
    private String lastName;
    private LocalDateTime dateOfBirth;
    private String phone;
    private String email;
    private String password;
    private boolean gender;
    private LocalDateTime createdAt;
    private boolean status;
    private String roleId;
}
