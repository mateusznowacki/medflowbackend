package pl.medflow.medflowbackend.aws.secrets;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;

import java.util.Map;

@Component
@RequiredArgsConstructor
class AwsSecretsLoader {

    private final AwsSecretsProperties props;

    public Map<String, String> load() {
        AwsCredentialsProvider creds = resolveCredentials();
        try (SecretsManagerClient client = SecretsManagerClient.builder()
                .region(Region.of(props.getRegion()))
                .credentialsProvider(creds)
                .build()) {

            String json = client.getSecretValue(
                    GetSecretValueRequest.builder()
                                         .secretId(props.getSecretName())
                                         .build())
                                .secretString();

            return Map.copyOf(new ObjectMapper()
                    .readValue(json, new TypeReference<>() {}));
        } catch (Exception e) {
            throw new IllegalStateException("Failed to load secrets from AWS", e);
        }
    }

    private AwsCredentialsProvider resolveCredentials() {
        String profile = props.getProfileName();
        if (profile == null || profile.isBlank()) {
            return DefaultCredentialsProvider.create();
        }
        try {
            return ProfileCredentialsProvider.create(profile);
        } catch (Exception e) {
            return DefaultCredentialsProvider.create();
        }
    }
}
