package code.services.token;

import code.exception.BadRequestException;
import code.exception.InvalidCredentialsException;
import code.exception.ResourceNotFoundException;
import code.exception.UnauthorizedException;
import code.model.dto.login.AuthResponseDTO;
import code.model.dto.login.LoginRequestDTO;
import code.model.dto.login.RefreshTokenRequestDTO;
import code.model.entity.users.UserEntity;
import code.repository.users.UserRepository;
import io.jsonwebtoken.Claims;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private BCryptPasswordEncoder encoder;
    @Autowired
    private JwTService jwtService;
    @Autowired
    private RefreshTokenStore refreshStore;

    public AuthResponseDTO login(LoginRequestDTO req) {
        // Lấy user từ database dựa trên email
        UserEntity user = userRepository.findByEmail(req.getEmail());
        if (user == null) {
            throw new InvalidCredentialsException("Email is not exist");
        }
        // So sánh password từ request với password đã mã hóa trong database
        if (!encoder.matches(req.getPassword(), user.getPassword())) {
            throw new InvalidCredentialsException("Password is incorrect");
        }

        String userId = String.valueOf(user.getUserId());
        String roleName = user.getRole().getRoleName();

        // Tạo access token
        String access = jwtService.generateAccessToken(userId, user.getEmail(), roleName);
        // Tạo refresh token
        JwTService.RefreshTokenBundle refreshBundle = jwtService.generateRefreshToken(userId);
        // Lưu jti của refresh token vào Redis
        refreshStore.save(userId, refreshBundle.jti());
        // Trả về access token và refresh token khi đăng nhập thành công
        AuthResponseDTO res = new AuthResponseDTO();
        res.setAccessToken(access);
        res.setRefreshToken(refreshBundle.token());
        return res;
    }

    public AuthResponseDTO refresh(RefreshTokenRequestDTO req) {
        //Claim là bước giải mã token để lấy thông tin bên trong token
        Claims claims = jwtService.parseClaims(req.getRefreshToken());
        // Kiểm tra loại token có phải là refresh token không
        if (!"refresh".equals(claims.get("type", String.class))) {
            throw new BadRequestException("Invalid token type");
        }
        // Kiểm tra refresh token có hết hạn không
        if (jwtService.isExpired(claims)) {
            throw new UnauthorizedException("Refresh token expired");
        }
        // Lấy userId và jti từ claims
        String userId = claims.getSubject();
        String jti = claims.getId();

        // So sánh jti từ token với jti lưu trong Redis để kiểm tra token có bị thu hồi không

        // Lấy jti đã lưu trong Redis
        String storedJti = refreshStore.getJti(userId);
        if (storedJti == null) {
            throw new UnauthorizedException("Session expired. Please login again");
        }
        // So sánh jti của token với jti đã lưu
        if (!storedJti.equals(jti)) {
            throw new UnauthorizedException("Refresh token is revoked");
        }

        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        // Tạo access token mới
        String newAccess = jwtService.generateAccessToken(userId, user.getEmail(), user.getRole().getRoleName());
        // Tạo refresh token mới
        JwTService.RefreshTokenBundle newRefresh = jwtService.generateRefreshToken(userId);
        refreshStore.save(userId, newRefresh.jti());
        // Trả về access token và refresh token mới
        AuthResponseDTO res = new AuthResponseDTO();
        res.setAccessToken(newAccess);
        res.setRefreshToken(newRefresh.token());
        return res;
    }

    // Logout thì đơn giản là chỉ cần xóa refresh token là được, mặc dù access token còn nhưng nó cũng tự hết hạn sau ... phút và không thể refresh được nữa
    public void logout(String userId) {
        refreshStore.delete(userId);
    }
}
