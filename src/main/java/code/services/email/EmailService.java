package code.services.email;

import jakarta.mail.MessagingException;

public interface EmailService {
    public void sendOtpEmail(String receiverEmail, String otp) throws MessagingException;
}
