package code.model.dto.s3;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FileUploadResponseDTO {
    private String fileUrl;
    private String fileName;
    private Long fileSize;
    private String message;
}
