package code.model.dto.users.req;

public record RequestChangePasswordDTO(
        String oldPassword,
        String newPassword,
        String rewriteNewPassword
) {
}
