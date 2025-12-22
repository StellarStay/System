package code.services.token;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
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

    @Autowired
    private JwTService jwtService;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        // Nó sẽ kiểm tra header Authorization của request để lấy JWT access token và dĩ nhiên bước này chỉ thực hiện sau khi user đã đăng nhập thành công và có token
        String auth = request.getHeader("Authorization");
        // Nếu header Authorization tồn tại và bắt đầu bằng "Bearer " thì tiến hành xử lý token
        if (auth != null && auth.startsWith("Bearer ")) {
            // Lấy phần token sau "Bearer "
            String token = auth.substring(7);
            try {
                // Giải mã token để lấy các thông tin bên trong token
                Claims claims = jwtService.parseClaims(token);
                // Nếu token là access token và chưa hết hạn thì tạo đối tượng Authentication và lưu vào SecurityContextHolder
                if ("access".equals(claims.get("type", String.class)) && !jwtService.isExpired(claims)) {
                    // Lấy userId và role từ claims
                    String userId = claims.getSubject();
                    String role = claims.get("role", String.class);
                    // Tạo danh sách authority từ role
                    var authorities = List.of(new SimpleGrantedAuthority("ROLE_" + role));
                    var authentication = new UsernamePasswordAuthenticationToken(userId, null, authorities);
                    // Lưu đối tượng Authentication vào SecurityContextHolder để sử dụng trong các phần khác của ứng dụng
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            } catch (Exception ignored) {
            }
        }
        // Tiếp tục chuỗi filter
        chain.doFilter(request, response);
    }
}
