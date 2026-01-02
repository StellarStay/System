package code.controller.s3;


import code.model.dto.s3.FileUploadResponseDTO;
import code.services.s3.S3Service;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;

@RestController
@RequestMapping("/api/s3")
@RequiredArgsConstructor
@Tag(name = "S3 File Management", description = "Upload/Delete files to AWS S3")
public class S3Controller {

    @Autowired
    private S3Service s3Service;

    @PostMapping(value = "/upload/room-image", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @Operation(summary = "Upload room image", description = "Upload hình ảnh phòng lên S3")
    public ResponseEntity<FileUploadResponseDTO> uploadRoomImage(
            @Parameter(
                description = "Image file to upload",
                required = true,
                content = @Content(mediaType = MediaType.MULTIPART_FORM_DATA_VALUE)
            )
            @RequestPart("file") MultipartFile file) throws IOException {

        // Validate file
        if (file.isEmpty()) {
            throw new IllegalArgumentException("File is empty");
        }

        // Validate file type (chỉ cho phép ảnh)
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }

        // Validate file size (max 5MB)
        if (file.getSize() > 5 * 1024 * 1024) {
            throw new IllegalArgumentException("File size must be less than 5MB");
        }

        String fileUrl = s3Service.uploadFile(file, "rooms");

        return ResponseEntity.ok(FileUploadResponseDTO.builder()
                .fileUrl(fileUrl)
                .fileName(file.getOriginalFilename())
                .fileSize(file.getSize())
                .message("Upload successful")
                .build());
    }

    @DeleteMapping("/delete")
    @Operation(summary = "Delete file from S3", description = "Xóa file khỏi S3")
    public ResponseEntity<String> deleteFile(@RequestParam("fileUrl") String fileUrl) {
        s3Service.deleteFile(fileUrl);
        return ResponseEntity.ok("File deleted successfully");
    }

    @GetMapping("/presigned-url")
    @Operation(summary = "Generate presigned URL", description = "Tạo URL tạm thời để download file private")
    public ResponseEntity<String> getPresignedUrl(
            @RequestParam("fileKey") String fileKey,
            @RequestParam(value = "expirationMinutes", defaultValue = "60") int expirationMinutes) {

        String url = s3Service.preSignUrl(fileKey, expirationMinutes);
        return ResponseEntity.ok(url);
    }

}
