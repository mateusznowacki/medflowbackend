package pl.medflow.medflowbackend.domain.aws.secrets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.Map;

@Component
public class AwsSecretsLoader {

    @Value("${aws.profile-name:}")
    private String profileName;

    @Value("${aws.region}")
    private String region;

    @Value("${aws.secret-name}")
    private String secretName;

    Map<String, String> load() {
        AwsCredentialsProvider creds = resolveCredentials();

        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(region))
                .credentialsProvider(creds)
                .build()) {

            String json = client.getSecretValue(
                    GetSecretValueRequest.builder()
                                         .secretId(secretName)
                                         .build())
                                .secretString();

            return Map.copyOf(new ObjectMapper()
                    .readValue(json, new TypeReference<>() {}));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load secrets from AWS", e);
        }
    }

    private AwsCredentialsProvider resolveCredentials() {
        if (profileName == null || profileName.isBlank()) {
            return DefaultCredentialsProvider.create();
        }
        try {
            return ProfileCredentialsProvider.create(profileName);
        } catch (Exception ex) {
            return DefaultCredentialsProvider.create();
        }
    }
}
