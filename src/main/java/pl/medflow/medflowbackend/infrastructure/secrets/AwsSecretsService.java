package pl.medflow.medflowbackend.infrastructure.secrets;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class AwsSecretsService implements AwsSecrets {

    private final AwsSecretsLoader loader;
    private Map<String, String> secrets;

    @PostConstruct
    void init() {
        this.secrets = loader.load();
    }

    public String getSecret(String key) {
        String value = secrets.get(key);
        if (value == null) {
            throw new IllegalArgumentException("Missing secret: " + key);
        }
        return value;
    }

    @Override
    public String getJwtPrivateKeyPem() {
        return getSecret("jwt_private_key");
    }

    @Override
    public String getJwtPublicKeyPem() {
        return getSecret("jwt_public_key");
    }

    @Override
    public String getS3BucketName() {
        return getSecret("s3_bucket_name");
    }
}
