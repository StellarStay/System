package code.services.token;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.util.Date;
import java.util.UUID;

@Service
public class JwTService {

    private SecretKey key;
    private long accessTtlMs;
    private long refreshTtlMs;

    public JwTService(
            @Value("${app.jwt.secret}") String secret,
            @Value("${app.jwt.access-ttl-ms}") long accessTtlMs,
            @Value("${app.jwt.refresh-ttl-ms}") long refreshTtlMs
    ) {
        this.key = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.accessTtlMs = accessTtlMs;
        this.refreshTtlMs = refreshTtlMs;
    }

    // Generate ra JWT access token với thông tin userId, email, role khi user đăng nhập thành công
    public String generateAccessToken(String userId, String email, String roleName) {
        long now = System.currentTimeMillis();
        return Jwts.builder()
                .setSubject(userId)
                .claim("email", email)
                .claim("roleName", roleName)
                .claim("type", "access")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + accessTtlMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();
    }
// Generate ra JWT refresh token với thông tin userId và jti (JWT ID) để dùng làm refresh token khi user đăng nhập thành công
    public RefreshTokenBundle generateRefreshToken(String userId) {
        long now = System.currentTimeMillis();
        String jti = UUID.randomUUID().toString();

        String token = Jwts.builder()
                .setSubject(userId)
                .setId(jti)
                .claim("type", "refresh")
                .setIssuedAt(new Date(now))
                .setExpiration(new Date(now + refreshTtlMs))
                .signWith(key, SignatureAlgorithm.HS256)
                .compact();

        return new RefreshTokenBundle(token, jti);
    }

    // Parse JWT token để lấy thông tin claims bên trong token
    public Claims parseClaims(String token) {
        return Jwts.parserBuilder().setSigningKey(key).build()
                .parseClaimsJws(token)
                .getBody();
    }

    public boolean isExpired(Claims claims) {
        return claims.getExpiration().before(new Date());
    }

    public record RefreshTokenBundle(String token, String jti) {}

}
