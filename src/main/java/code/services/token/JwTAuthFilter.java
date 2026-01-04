package code.services.token;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

@Component
public class JwTAuthFilter extends OncePerRequestFilter {

    private static final Logger logger = LoggerFactory.getLogger(JwTAuthFilter.class);

    @Autowired
    private JwTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Lấy token từ header Authorization hoặc Cookie
        String token = null;
        String auth = request.getHeader("Authorization");

        // Log để debug
        logger.debug("Processing request to: {} {}", request.getMethod(), request.getRequestURI());

        // Ưu tiên lấy từ Authorization header (cho Postman, mobile app, etc.)
        if (auth != null && auth.startsWith("Bearer ")) {
            token = auth.substring(7);
            logger.debug("Token found in Authorization header");
        }
        // Nếu không có header, thử lấy từ Cookie (cho Swagger, Browser)
        else if (request.getCookies() != null) {
            for (jakarta.servlet.http.Cookie cookie : request.getCookies()) {
                if ("ACCESS_TOKEN".equals(cookie.getName())) {
                    token = cookie.getValue();
                    logger.debug("Token found in Cookie");
                    break;
                }
            }
        }

        // Nếu có token thì xử lý
        if (token != null) {
            try {
                // Giải mã token để lấy các thông tin bên trong token
                Claims claims = jwtService.parseClaims(token);

                // Kiểm tra type và expiration
                String tokenType = claims.get("type", String.class);
                boolean isExpired = jwtService.isExpired(claims);

                logger.debug("Token type: {}, Expired: {}", tokenType, isExpired);

                // Nếu token là access token và chưa hết hạn thì tạo đối tượng Authentication và lưu vào SecurityContextHolder
                if ("access".equals(tokenType) && !isExpired) {
                    // Lấy userId và roleName từ claims
                    String userId = claims.getSubject();
                    String roleName = claims.get("roleName", String.class);

                    logger.debug("Authenticated user: {} with roleName: {}", userId, roleName);

                    // Tạo danh sách authority từ roleName
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + roleName));
                    var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    // Lưu đối tượng Authentication vào SecurityContextHolder để sử dụng trong các phần khác của ứng dụng
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                } else {
                    logger.warn("Invalid token type or expired token for request: {}", request.getRequestURI());
                }
            } catch (Exception e) {
                logger.warn("JWT validation failed for request {}: {}", request.getRequestURI(), e.getMessage());
            }
        } else {
            logger.debug("No token found (neither Authorization header nor Cookie) for request: {}", request.getRequestURI());
        }

        // Tiếp tục chuỗi filter
        chain.doFilter(request, response);
    }
}
