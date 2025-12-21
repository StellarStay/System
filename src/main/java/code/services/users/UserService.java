package code.services.users;

import code.model.dto.users.UserRequestDTO;
import code.model.dto.users.UserResponseDTO;
import code.model.entity.users.UserEntity;
import org.apache.catalina.User;

import java.util.List;

public interface UserService {
    boolean insertUser(UserRequestDTO userRequestDTO);
    boolean updateUser(String userId, UserRequestDTO userRequestDTO);
    boolean deleteUser(String userId);
    UserEntity getUser(String userId);
    List<UserEntity> getUsers();
    boolean isEmailExists(String email);
    UserEntity findByEmail(String email);


}
