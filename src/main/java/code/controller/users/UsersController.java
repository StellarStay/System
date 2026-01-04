package code.controller.users;

import code.model.dto.users.UserRequestDTO;
import code.model.dto.users.UserResponseDTO;
import code.model.entity.users.UserEntity;
import code.services.users.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/users")
public class UsersController {
    @Autowired
    private UserService userService;

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

    @GetMapping("/get_user/{userId}")
    public ResponseEntity<UserResponseDTO> getUser(@PathVariable String userId) {
        UserEntity foundUser = userService.getUser(userId);
        UserResponseDTO userResponseDTO = new UserResponseDTO();
        if (foundUser != null) {
            userResponseDTO.setUserId(foundUser.getUserId());
            userResponseDTO.setIdCard(foundUser.getIdCard());
            userResponseDTO.setFirstName(foundUser.getFirstName());
            userResponseDTO.setLastName(foundUser.getLastName());
            userResponseDTO.setDateOfBirth(foundUser.getDateOfBirth());
            userResponseDTO.setPhone(foundUser.getPhone());
            userResponseDTO.setEmail(foundUser.getEmail());
            userResponseDTO.setPassword(foundUser.getPassword());
            userResponseDTO.setGender(foundUser.isGender());
            userResponseDTO.setCreatedAt(foundUser.getCreatedAt());
            userResponseDTO.setStatus(foundUser.isStatus());
            userResponseDTO.setRoleId(foundUser.getRole().getRoleId());
            return ResponseEntity.ok(userResponseDTO);
        }
        else  {
            return ResponseEntity.notFound().build();
        }
    }

    @PreAuthorize("hasRole('ADMIN')")
    @GetMapping("/get_all_users")
    public ResponseEntity<List<UserResponseDTO>> getAllUsers() {
        List<UserEntity> userEntities = userService.getUsers();
        List<UserResponseDTO> userResponseDTOs = userEntities.stream().map(userEntity -> {
            UserResponseDTO dto = new UserResponseDTO();
            dto.setUserId(userEntity.getUserId());
            dto.setIdCard(userEntity.getIdCard());
            dto.setFirstName(userEntity.getFirstName());
            dto.setLastName(userEntity.getLastName());
            dto.setDateOfBirth(userEntity.getDateOfBirth());
            dto.setPhone(userEntity.getPhone());
            dto.setEmail(userEntity.getEmail());
            dto.setPassword(userEntity.getPassword());
            dto.setGender(userEntity.isGender());
            dto.setCreatedAt(userEntity.getCreatedAt());
            dto.setStatus(userEntity.isStatus());
            dto.setRoleId(userEntity.getRole().getRoleId());
            return dto;
        }).toList();
        return ResponseEntity.ok(userResponseDTOs);
    }

}
