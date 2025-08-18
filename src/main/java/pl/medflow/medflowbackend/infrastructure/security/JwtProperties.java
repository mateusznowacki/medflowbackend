// security/JwtProperties.java
package pl.medflow.medflowbackend.domain.security;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter @Setter
@Component
@ConfigurationProperties(prefix = "app.jwt")
public class JwtProperties {
    private String secret;
    private Access access = new Access();
    private Refresh refresh = new Refresh();
    private Cookie cookie = new Cookie();

    @Getter @Setter public static class Access {
        private int expirationMinutes;
        private String issuer;
    }
    @Getter @Setter public static class Refresh {
        private int expirationDays;
        private String issuer;
    }
    @Getter @Setter public static class Cookie {
        private String name;
        private String path;
        private String sameSite;
        private boolean secure;
    }
}
