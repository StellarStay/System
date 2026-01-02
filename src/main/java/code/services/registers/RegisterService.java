package code.services.registers;

import code.model.dto.users.UserRequestDTO;
import jakarta.mail.MessagingException;

import java.util.Map;

public interface RegisterService {
    public Map<String, String> saveTempUser(UserRequestDTO userRequestDTO) throws MessagingException;
    public String verifyOtp(String verifyToken, String otp);
}
