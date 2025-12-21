package code.services.email;

public interface EmailService {
    public void sendOtpEmail(String receiverEmail, String otp);
}
