package code.config.data_init;

import code.model.entity.users.RoleEntity;
import code.model.entity.users.UserEntity;
import code.repository.users.UserRepository;
import code.repository.users.RoleRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Profile;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import jakarta.annotation.PostConstruct;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.UUID;

@Component
@Profile({"local", "dev"})
@RequiredArgsConstructor
public class DataInitAdmin {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @PostConstruct
    public void initAdmin() {
        String adminEmail = "admin@gmail.com";

        // Kiểm tra xem admin đã tồn tại chưa
        if (userRepository.existsByEmail(adminEmail)) {
            System.out.println("⚠️  ADMIN user already exists. Skipping initialization.");
            return;
        }

        // Lấy role ADMIN từ DB (được insert bởi migration V5)
        RoleEntity adminRole = roleRepository.findById("admin")
                .orElseThrow(() ->
                        new RuntimeException("❌ admin role not found. Please check if Flyway migration V5 ran successfully!")
                );

        // Tạo user ADMIN
        UserEntity admin = new UserEntity();
        admin.setUserId(UUID.randomUUID().toString());
        admin.setIdCard("000000000000");
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setDateOfBirth(LocalDate.of(1990, 1, 1));
        admin.setPhone("0900000000");
        admin.setEmail(adminEmail);
        admin.setPassword(passwordEncoder.encode("admin123"));
        admin.setGender(true);
        admin.setStatus(true);
        admin.setCreatedAt(LocalDateTime.now());
        admin.setRole(adminRole);

        userRepository.save(admin);

        System.out.println("✅ ADMIN user created successfully!");
        System.out.println("📧 Email: " + adminEmail);
        System.out.println("🔑 Password: admin123");
    }
}
