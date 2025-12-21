package code.services.register_login;

import code.model.dto.users.UserRequestDTO;
import code.model.entity.users.UserEntity;
import jakarta.mail.MessagingException;

public interface RegisterService {
    public String saveTempUser(UserRequestDTO userRequestDTO) throws MessagingException;
    public String verifyOtp(String email, String otp);
}
