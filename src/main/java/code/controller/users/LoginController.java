package code.controller.users;

import code.exception.UnauthorizedException;
import code.model.dto.login.AuthResponseDTO;
import code.model.dto.login.LoginRequestDTO;
import code.model.dto.login.RefreshTokenRequestDTO;
import code.model.dto.users.res.UserResponseDTO;
import code.services.token.AuthService;
import code.services.users.UserService;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class LoginController {
    @Autowired
    private AuthService authService;
    @Autowired
    private UserService userService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponseDTO> login(@RequestBody LoginRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.login(request);

        // Tự động set access token vào cookie để Swagger/Browser tự động gửi
        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", authResponse.getAccessToken());
        accessTokenCookie.setHttpOnly(true); // Bảo mật, JavaScript không đọc được
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // 15 phút (giống JWT expiration)
        response.addCookie(accessTokenCookie);

        // Set refresh token vào cookie
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", authResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(authResponse);
    }

    // API này dùng để lấy access token mới khi access token cũ hết hạn bằng cách sử dụng refresh token để trả về access token mới và set vào cookie
    @PostMapping("/refresh")
    public ResponseEntity<AuthResponseDTO> refresh(@RequestBody RefreshTokenRequestDTO request, HttpServletResponse response) {
        AuthResponseDTO authResponse = authService.refresh(request);

        // Tự động set access token mới vào cookie
        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", authResponse.getAccessToken());
        accessTokenCookie.setHttpOnly(true);
        accessTokenCookie.setPath("/");
        accessTokenCookie.setMaxAge(15 * 60); // 15 phút (hoặc theo cấu hình của bạn)
        response.addCookie(accessTokenCookie);

        // Set refresh token mới vào cookie
        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", authResponse.getRefreshToken());
        refreshTokenCookie.setHttpOnly(true);
        refreshTokenCookie.setPath("/");
        refreshTokenCookie.setMaxAge(7 * 24 * 60 * 60); // 7 ngày
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(Authentication authentication, HttpServletResponse response) {

        String userId = (String) authentication.getPrincipal();
        authService.logout(userId);

        // Xóa cookies
        Cookie accessTokenCookie = new Cookie("ACCESS_TOKEN", null);
        accessTokenCookie.setMaxAge(0);
        accessTokenCookie.setPath("/");
        response.addCookie(accessTokenCookie);

        Cookie refreshTokenCookie = new Cookie("REFRESH_TOKEN", null);
        refreshTokenCookie.setMaxAge(0);
        refreshTokenCookie.setPath("/");
        response.addCookie(refreshTokenCookie);

        return ResponseEntity.ok("Logged out successfully");
    }

    @GetMapping("/me")
    public ResponseEntity<UserResponseDTO> getCurrentUser(Authentication authentication) {

        String userId = (String) authentication.getPrincipal();
        UserResponseDTO user = userService.getUserResponseDTO(userId);
        return ResponseEntity.ok(user);
    }
}