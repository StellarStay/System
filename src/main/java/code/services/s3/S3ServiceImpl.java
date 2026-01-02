package code.services.s3;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.ObjectCannedACL;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.services.s3.presigner.S3Presigner;
import software.amazon.awssdk.services.s3.presigner.model.GetObjectPresignRequest;

import java.io.IOException;
import java.time.Duration;
import java.util.UUID;

@Service
public class S3ServiceImpl implements S3Service{

    @Value("${aws.s3.bucket.name}")
    private String bucketName;
    @Value("${aws.s3.region}")
    private String region;

    @Autowired
    private S3Client s3Client;


    @Override
    public String uploadFile(MultipartFile file, String folder) throws IOException {
        String originalFilename = file.getOriginalFilename();
        assert originalFilename != null;
        String extension = originalFilename.substring(originalFilename.lastIndexOf("."));
        String fileName = folder + "/" + UUID.randomUUID().toString() + extension;

        // Upload lên S3
        PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(fileName)
                .contentType(file.getContentType())
                .acl(ObjectCannedACL.PUBLIC_READ) // Public file (ai cũng xem được)
                .build();

        s3Client.putObject(putObjectRequest, RequestBody.fromBytes(file.getBytes()));

        // Trả về URL công khai
        return String.format("https://%s.s3.%s.amazonaws.com/%s", bucketName, region, fileName);
    }

    @Override
    public void deleteFile(String fileUrl) {
        // Extract key từ URL
        // URL: https://stellarstay-room-images.s3.ap-southeast-1.amazonaws.com/rooms/abc.jpg
        // Key: rooms/abc.jpg
        String key = fileUrl.substring(fileUrl.indexOf(".com/") + 5);

        DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();

        s3Client.deleteObject(deleteObjectRequest);
    }

    @Override
    public String preSignUrl(String fileKey, int expireMinutes) {
        S3Presigner preSigner = S3Presigner.create();

        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(fileKey)
                .build();

        GetObjectPresignRequest presignRequest = GetObjectPresignRequest.builder()
                .signatureDuration(Duration.ofMinutes(expireMinutes))
                .getObjectRequest(getObjectRequest)
                .build();

        String url = preSigner.presignGetObject(presignRequest).url().toString();
        preSigner.close();

        return url;
    }
}
