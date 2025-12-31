package code.services.registers;

import code.model.dto.users.UserRequestDTO;
import code.services.email.EmailService;
import code.services.users.UserService;
import code.util.RandomId;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.mail.MessagingException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class RegisterServiceImpl implements RegisterService {
    @Autowired
    private UserService userService;
    @Autowired
    private EmailService emailService;
    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private RedisTemplate<Object, Object> redisTemplate;
    @Autowired
    private ObjectMapper objectMapper;

    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    int max_length_otp = 6;

    private String generateOtp() {
        String otpRandom;
        otpRandom = RandomId.generateOtp(max_length_otp);
        return otpRandom;
    }

    // Serialize user object thành chuỗi JSON để lưu vào Redis
    private String serializeUser(UserRequestDTO userEntity) {
        try{
            return objectMapper.writeValueAsString(userEntity);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    // Deserialize chuỗi JSON từ Redis thành object User
    private UserRequestDTO deserializeUser(String userJson) {
        try{
            return objectMapper.readValue(userJson, UserRequestDTO.class);
        }
        catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }

    @Override
    public String saveTempUser(UserRequestDTO userRequestDTO) throws MessagingException {
        // Kiểm tra xem email đã tồn tại trong redis hay chưa
        if (stringRedisTemplate.opsForValue().get("user:temp:" + userRequestDTO.getEmail()) != null) {
            return "Email already exists in pending registrations.";
        }
        // Mã hóa mật khẩu trước khi lưu tạm
        String encodedPassword = passwordEncoder.encode(userRequestDTO.getPassword());
        // Tạo OTP
        String otp = generateOtp();
        // Tạo ra một user tạm thời để lưu vào redis với dạng json
        UserRequestDTO tempUser = new UserRequestDTO();
        tempUser.setIdCard(userRequestDTO.getIdCard());
        tempUser.setPhone(userRequestDTO.getPhone());
        tempUser.setDateOfBirth(userRequestDTO.getDateOfBirth());
        tempUser.setRoleId(userRequestDTO.getRoleId());
        tempUser.setGender(userRequestDTO.isGender());
        tempUser.setEmail(userRequestDTO.getEmail());
        tempUser.setPassword(encodedPassword);
        tempUser.setFirstName(userRequestDTO.getFirstName());
        tempUser.setLastName(userRequestDTO.getLastName());

        String tempUserJson = serializeUser(tempUser);
        // Lưu user tạm thời và otp vào redis với thời gian hết hạn là 5 phút
        assert tempUserJson != null;
        stringRedisTemplate.opsForValue().set("user:temp:" + userRequestDTO.getEmail(), tempUserJson, 5, TimeUnit.MINUTES);
        redisTemplate.opsForValue().set("otp:" + userRequestDTO.getEmail(), otp, 5, TimeUnit.MINUTES);

        // Gửi otp về email
        emailService.sendOtpEmail(userRequestDTO.getEmail(), otp);
        return "Registration successful! Please check your email for the OTP.";
    }


    @Override
    public String verifyOtp(String email, String otp) {
        // Lấy OTP từ Redis
        String storedOtp = (String) redisTemplate.opsForValue().get("otp:" + email);
        if (storedOtp == null) {
            return "OTP has expired or does not exist.";
        }

        // Compare OTP
        if (storedOtp.equals(otp)) {
            // Lấy thông tin user vừa đăng ký tạm thời từ Redis
            String tempUserJson = stringRedisTemplate.opsForValue().get("user:temp:" + email);
            if (tempUserJson != null){
                UserRequestDTO registeredUser = deserializeUser(tempUserJson);
                // Lưu user chính thức vào database
                userService.insertUser(registeredUser);
                // Xóa thông tin người dùng và otp được lưu tạm thời trong Redis
                stringRedisTemplate.delete("user:temp:" + email);
                redisTemplate.delete("otp:" + email);
                return "OTP verified successfully! Your registration is complete.";
            }
        }

        return "Invalid OTP. Please try again.";
    }
}
