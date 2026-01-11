package code.model.dto.users.res;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class VerifyTokenRequestOTP {
    @NotBlank
    private String verificationToken;

    @NotBlank
    @Pattern(regexp = "^\\d{6}$")
    private String otp;
}
