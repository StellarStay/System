package code.controller.users;

import code.exception.BadRequestException;
import code.exception.ResourceNotFoundException;
import code.mapper.UserMapper;
import code.model.dto.users.req.RequestChangePasswordDTO;
import code.model.dto.users.req.UserRequestDTO;
import code.model.dto.users.req.UserRequestForAdminDTO;
import code.model.dto.users.res.UserResponseDTO;
import code.model.entity.users.UserEntity;
import code.services.users.UserService;
import code.services.token.JwTService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserService userService;

    @Autowired
    private JwTService jwTService;

    @Autowired
    private UserMapper userMapper;

    @PreAuthorize("hasRole('ADMIN')")
    @PostMapping("/create_user")
    public ResponseEntity<String> createUser(@RequestBody UserRequestDTO userRequestDTO) {
        boolean isCreated = userService.insertUser(userRequestDTO);
        if (isCreated) {
            return ResponseEntity.ok("User created successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to create user");
        }

    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_all_users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserEntity> userEntities = userService.getAllUsers();
        if (userEntities == null) {
            throw new ResourceNotFoundException("No users found");
        }
        List<UserResponseDTO> userResponseDTOs = new ArrayList<>();
        for ( UserEntity userEntity : userEntities) {
            UserResponseDTO dto = userMapper.toResponseDTO(userEntity);
            userResponseDTOs.add(dto);
        }
        return ResponseEntity.ok(userResponseDTOs);
    }

    @GetMapping("/get_user/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String userId) {
        if (userId == null){
            throw new BadRequestException("UserId is null");
        }
        UserEntity foundUser = userService.getUser(userId);
        if (foundUser == null){
            throw new ResourceNotFoundException("User not found");
        }
        UserResponseDTO dto = userMapper.toResponseDTO(foundUser);
        return ResponseEntity.ok(dto);

    }

    @PutMapping("/update/user_user")
    public ResponseEntity<String> updateForUser(
            Authentication authentication,
            @RequestBody UserRequestDTO userRequestDTO) {

        // Lấy userId từ Authentication (đã được parse từ cookie bởi JwTAuthFilter)
        String userId = (String) authentication.getPrincipal();

        boolean isUpdated = userService.updateUserForUser(userId, userRequestDTO);
        if (isUpdated) {
            return ResponseEntity.ok("User updated successfully");
        } else {
            return ResponseEntity.status(500).body("Failed to update user");
        }
    }

    @PutMapping("/update/admin_user")
    public ResponseEntity<String> updateAdminForUser(Authentication authentication, @RequestBody UserRequestForAdminDTO userRequestDTO) {

        // Lấy userId từ Authentication (đã được parse từ cookie bởi JwTAuthFilter)
        String userId = (String) authentication.getPrincipal();

        boolean isUpdated = userService.updateUserForAdmin(userId, userRequestDTO);
        if (isUpdated) {
            return ResponseEntity.ok("User updated successfully");
        }
        else {
            return ResponseEntity.status(500).body("Failed to update user");
        }
    }

    @PutMapping("/change_password")
    public ResponseEntity<String> changePassword(Authentication authentication, @RequestBody RequestChangePasswordDTO requestDTO) {
        // Lấy userId từ Authentication (đã được parse từ cookie bởi JwTAuthFilter)
        String userId = (String) authentication.getPrincipal();

        boolean isChanged = userService.changePassword(userId, requestDTO);
        if (isChanged) {
            return ResponseEntity.ok("Password changed successfully");
        }
        else {
            return ResponseEntity.status(500).body("Failed to change password");
        }
    }


}
