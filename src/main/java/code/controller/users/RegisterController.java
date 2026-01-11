package code.controller.users;

import code.model.dto.users.req.UserRequestDTO;
import code.model.dto.users.res.VerifyTokenRequestOTP;
import code.services.registers.RegisterService;
import code.services.users.UserService;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/auth/register")
public class RegisterController {

    @Autowired
    private UserService userService;
    @Autowired
    private RegisterService registerService;

    @PostMapping("/fill_information")
    public ResponseEntity<?> register(@RequestBody UserRequestDTO userRequestDTO) throws MessagingException {
        Map<String, String> result = registerService.saveTempUser(userRequestDTO);
        return ResponseEntity.ok(result);
    }

    @PostMapping("/verify-otp")
    public ResponseEntity<?> verifyOtp(@RequestBody VerifyTokenRequestOTP request) {
        String result = registerService.verifyOtp(request.getVerificationToken(), request.getOtp());
        return ResponseEntity.ok(Map.of("message", result));
    }
}
