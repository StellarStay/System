package code.services.registers;

import code.model.dto.users.UserRequestDTO;
import jakarta.mail.MessagingException;

public interface RegisterService {
    public String saveTempUser(UserRequestDTO userRequestDTO) throws MessagingException;
    public String verifyOtp(String email, String otp);
}
