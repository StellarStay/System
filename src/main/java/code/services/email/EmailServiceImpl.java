package code.services.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

@Service
public class EmailServiceImpl implements EmailService{
    @Autowired
    private JavaMailSender mailSender;

    @Override
    public void sendOtpEmail(String receiverEmail, String otp) throws MessagingException {
        // Tạo MimeMessage
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);  // true: cho phép đính kèm và sử dụng HTML

        helper.setFrom("stellastay183@gmail.com");
        helper.setTo(receiverEmail);
        helper.setSubject("Your OTP Code");

        String htmlContent = "<html><body style='font-family: Arial, sans-serif; background-color: #1E3A3A; padding: 20px;'>"
                + "<div style='max-width: 600px; margin: 0 auto; background-color: #ffffff; border-radius: 8px; padding: 20px; box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);'>"
                + "<h2 style='text-align: center; color: #333;'>Your OTP Code</h2>"
                + "<h3 style='text-align: center; color: #D8A89E; font-size: 24px; font-weight: bold;'>" + otp + "</h3>"
                + "<p style='text-align: center; color: #666;'>This OTP is valid for 5 minutes.</p>"
                + "<hr style='border: 1px solid #f0f0f0;'>"
                + "<p style='text-align: center; font-size: 14px; color: #999;'>Thank you for using our service!</p>"
                + "<p style='text-align: center; font-size: 12px; color: #bbb;'>If you did not request this, please ignore this email.</p>"
                + "</div>"
                + "</body></html>";


        // Đặt nội dung email dưới dạng HTML
        helper.setText(htmlContent, true);  // true: Đảm bảo email sẽ được gửi dưới dạng HTML

        // Gửi email
        mailSender.send(message);
        System.out.println("OTP email sent to " + receiverEmail + " with OTP: " + otp);
    }
}
