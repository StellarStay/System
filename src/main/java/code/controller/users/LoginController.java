package code.controller.users;

import code.model.dto.login.AuthResponseDTO;
import code.model.dto.login.LoginRequestDTO;
import code.model.dto.login.RefreshTokenRequestDTO;
import code.services.login.AuthService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {

    @Autowired
    private AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request) {
        AuthResponseDTO response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request) {
        AuthResponseDTO response = authService.refresh(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.ok("No user logged in");
        }

        String userId = (String) authentication.getPrincipal();
        authService.logout(userId);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<String> getCurrentUser(Authentication authentication) {
        if (authentication == null) {
            return ResponseEntity.status(401).body("Not authenticated");
        }

        String userId = (String) authentication.getPrincipal();

        return ResponseEntity.ok("User ID: " + userId);
    }
}