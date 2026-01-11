package code.services.users;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.mapper.UserMapper;
import code.model.dto.users.req.RequestChangePasswordDTO;
import code.model.dto.users.req.UserRequestDTO;
import code.model.dto.users.req.UserRequestForAdminDTO;
import code.model.dto.users.res.UserResponseDTO;
import code.model.entity.users.RoleEntity;
import code.model.entity.users.UserEntity;
import code.repository.users.RoleRepository;
import code.repository.users.UserRepository;
import code.util.RandomId;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
public class UserServiceImpl implements UserService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private UserMapper userMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    int max_length_id = 8;
    @Autowired
    private RoleRepository roleRepository;

    private String generateUserId() {
        String randomId;
        do{
            randomId = RandomId.generateRoomId(max_length_id);
        }while(userRepository.findById(randomId).isPresent());
        return randomId;
    }

    @Override
    public boolean insertUser(UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null) {
            throw new BadRequestException("UserRequestDTO is null");
        }
        UserEntity userEntity = new UserEntity();
        userEntity.setUserId(generateUserId());
        userEntity.setIdCard(userRequestDTO.getIdCard());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setDateOfBirth(userRequestDTO.getDateOfBirth());
        userEntity.setPhone(userRequestDTO.getPhone());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setPassword(userRequestDTO.getPassword());
        userEntity.setGender(userRequestDTO.isGender());
        userEntity.setCreatedAt(LocalDateTime.now());

        RoleEntity roleEntity = new RoleEntity();
        roleEntity.setRoleId(userRequestDTO.getRoleId());

        userEntity.setRole(roleEntity);
        userEntity.setStatus(true);
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean updateUserForUser(String userId, UserRequestDTO userRequestDTO) {
        if (userRequestDTO == null || userId == null) {
            throw new BadRequestException("UserRequestDTO or UserId is null");
        }
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            throw new ResourceNotFoundException("User not found");
        }

        userEntity.setIdCard(userRequestDTO.getIdCard());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setDateOfBirth(userRequestDTO.getDateOfBirth());
        userEntity.setPhone(userRequestDTO.getPhone());
        userEntity.setEmail(userRequestDTO.getEmail());
        //  Làm service đổi password riêng chỗ khác
        userEntity.setPassword(userEntity.getPassword());
        userEntity.setGender(userRequestDTO.isGender());

        // Cố định role là user
        RoleEntity roleEntity = roleRepository.findById("user")
                .orElseThrow(() -> new ResourceNotFoundException("Role 'user' not found"));

        userEntity.setRole(roleEntity);
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean updateUserForAdmin(String userId, UserRequestForAdminDTO userRequestDTO) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        userEntity.setIdCard(userRequestDTO.getIdCard());
        userEntity.setFirstName(userRequestDTO.getFirstName());
        userEntity.setLastName(userRequestDTO.getLastName());
        userEntity.setDateOfBirth(userRequestDTO.getDateOfBirth());
        userEntity.setPhone(userRequestDTO.getPhone());
        userEntity.setEmail(userRequestDTO.getEmail());
        userEntity.setPassword(passwordEncoder.encode(userRequestDTO.getPassword()));
        userEntity.setGender(userRequestDTO.isGender());

        RoleEntity roleEntity = roleRepository.findById(userRequestDTO.getRoleId())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found with id: " + userRequestDTO.getRoleId()));

        userEntity.setRole(roleEntity);
        userEntity.setStatus(userRequestDTO.isStatus());
        userRepository.save(userEntity);
        return true;
    }

    @Override
    public boolean deleteUser(String userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        userRepository.delete(userEntity);
        return true;
    }

    @Override
    public UserEntity getUser(String userId) {
        return userRepository.findById(userId).orElse(null);
    }

    @Override
    public UserResponseDTO getUserResponseDTO(String userId) {
        if (userId == null) {
            throw new BadRequestException("UserId is null");
        }
        UserEntity userEntity = userRepository.findById(userId).orElse(null);
        if (userEntity == null) {
            return null;
        }

        return userMapper.toResponseDTO(userEntity);
    }

    @Override
    public List<UserEntity> getAllUsers() {
        return userRepository.findAll();
    }

    @Override
    public boolean isEmailExists(String email) {
        return userRepository.existsByEmail(email);
    }

    @Override
    public UserEntity findByEmail(String email) {
        return userRepository.findByEmail(email);
    }

    @Override
    public boolean changePassword(String userId, RequestChangePasswordDTO requestDTO) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        // So sánh mật khẩu cũ với mật khẩu trong db (sử dụng BCrypt matches)
        if (!passwordEncoder.matches(requestDTO.oldPassword(), userEntity.getPassword())) {
            throw new BadRequestException("Old password is incorrect");
        }

        // So sánh mật khẩu cũ với mật khẩu mới không được trùng nhau
        if (requestDTO.oldPassword().equals(requestDTO.newPassword())) {
            throw new BadRequestException("New password must be different from old password");
        }

        // So sánh mật khẩu mới và mật khẩu nhập lại phải giống nhau
        if (!requestDTO.newPassword().equals(requestDTO.rewriteNewPassword())) {
            throw new BadRequestException("New password and rewrite new password do not match");
        }

        // Mã hóa và lưu mật khẩu mới vào db
        String newPasswordEncoded = passwordEncoder.encode(requestDTO.newPassword());
        userEntity.setPassword(newPasswordEncoded);
        userRepository.save(userEntity);
        return true;
    }

}
