package pl.medflow.medflowbackend.aws.s3;

import jakarta.annotation.PostConstruct;
import org.springframework.stereotype.Service;
import pl.medflow.medflowbackend.aws.secrets.AwsSecrets;
import software.amazon.awssdk.core.sync.RequestBody;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.*;

import java.io.InputStream;
import java.nio.file.Path;

@Service
public class AwsS3StorageService implements AwsS3Storage {

    private final S3Client s3;
    private final AwsSecrets secrets;
    private String bucketName;

    public AwsS3StorageService(S3Client s3, AwsSecrets secrets) {
        this.s3 = s3;
        this.secrets = secrets;
    }

    @PostConstruct
    void init() {
        this.bucketName = secrets.getS3BucketName();
    }

    @Override
    public void upload(String key, InputStream data, long contentLength, String contentType) {
        PutObjectRequest request = PutObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .contentLength(contentLength)
                .contentType(contentType)
                .build();
        s3.putObject(request, RequestBody.fromInputStream(data, contentLength));
    }

    @Override
    public InputStream download(String key) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        return s3.getObject(request);
    }

    @Override
    public void downloadToFile(String key, Path destination) {
        GetObjectRequest request = GetObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.getObject(request, destination);
    }

    @Override
    public void delete(String key) {
        DeleteObjectRequest request = DeleteObjectRequest.builder()
                .bucket(bucketName)
                .key(key)
                .build();
        s3.deleteObject(request);
    }

    @Override
    public void update(String key, InputStream data, long contentLength, String contentType) {
        delete(key);
        upload(key, data, contentLength, contentType);
    }
}
