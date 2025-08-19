package pl.medflow.medflowbackend.infrastructure.s3;

import java.io.InputStream;
import java.nio.file.Path;

public interface AwsS3Storage {
    void upload(String key, InputStream data, long contentLength, String contentType);

    InputStream download(String key);

    void downloadToFile(String key, Path destination);

    void delete(String key);

    void update(String key, InputStream data, long contentLength, String contentType);
}
