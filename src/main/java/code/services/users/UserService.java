package code.services.users;

import code.model.dto.users.req.RequestChangePasswordDTO;
import code.model.dto.users.req.UserRequestDTO;
import code.model.dto.users.req.UserRequestForAdminDTO;
import code.model.dto.users.res.UserResponseDTO;
import code.model.entity.users.UserEntity;

import java.util.List;

public interface UserService {
    boolean insertUser(UserRequestDTO userRequestDTO);
    boolean updateUserForUser(String userId, UserRequestDTO userRequestDTO);
    boolean updateUserForAdmin(String userId, UserRequestForAdminDTO userRequestDTO);
    boolean deleteUser(String userId);
    UserEntity getUser(String userId);
    UserResponseDTO getUserResponseDTO(String userId);
    List<UserEntity> getAllUsers();
    boolean isEmailExists(String email);
    UserEntity findByEmail(String email);

    boolean changePassword(String userId, RequestChangePasswordDTO requestDTO);


}