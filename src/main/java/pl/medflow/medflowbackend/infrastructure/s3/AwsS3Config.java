package pl.medflow.medflowbackend.domain.aws.s3;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import software.amazon.awssdk.auth.credentials.AwsCredentialsProvider;
import software.amazon.awssdk.auth.credentials.DefaultCredentialsProvider;
import software.amazon.awssdk.auth.credentials.ProfileCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;

@Configuration
public class AwsS3Config {

    @Value("${aws.profile-name:}")
    private String profileName;

    @Value("${aws.region}")
    private String region;

    @Bean
    public S3Client s3Client() {
        return S3Client.builder()
                .region(Region.of(region))
                .credentialsProvider(resolveCredentials())
                .build();
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
