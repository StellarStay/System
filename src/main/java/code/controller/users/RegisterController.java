package code.controller.users;

import code.model.dto.users.UserRequestDTO;
import code.model.entity.users.UserEntity;
import code.services.register_login.RegisterService;
import code.services.users.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/register")
public class RegisterController {

    @Autowired
    private UserService userService;
    @Autowired
    private RegisterService registerService;

    @PostMapping("/fill_information")
    public ResponseEntity <String> fillInformation(@RequestBody UserRequestDTO userRequestDTO) throws MessagingException {
        return ResponseEntity.ok(registerService.saveTempUser(userRequestDTO));
    }

    @PostMapping("/verify_otp")
    public ResponseEntity <String> verifyOtp(@RequestParam String email, @RequestParam String otp) {
        return ResponseEntity.ok(registerService.verifyOtp(email, otp));
    }
}
