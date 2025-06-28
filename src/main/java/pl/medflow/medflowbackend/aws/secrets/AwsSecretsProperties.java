
package pl.medflow.medflowbackend.aws.secrets;

import lombok.Getter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Component
@ConfigurationProperties(prefix = "aws")
class AwsSecretsProperties {
    private String profileName;
    private String region;
    private String secretName;

    public void setProfileName(String profileName) {
        this.profileName = profileName;
    }

    public void setRegion(String region) {
        this.region = region;
    }

    public void setSecretName(String secretName) {
        this.secretName = secretName;
    }
}
