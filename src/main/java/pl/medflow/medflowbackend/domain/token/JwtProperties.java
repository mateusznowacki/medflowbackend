package pl.medflow.medflowbackend.domain.token;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {
    private Access access = new Access();
    private Refresh refresh = new Refresh();
    private Cookie cookie = new Cookie();

    @Getter
    @Setter
    public static class Access {
        private int expirationSeconds;
        private String issuer;

        public int accessExpirationSeconds() {
            if (expirationSeconds > 0) return expirationSeconds;
            return 900;
        }
    }

    @Getter
    @Setter
    public static class Refresh {
        private int expirationSeconds;
        private String issuer;

        public int refreshExpirationSeconds() {
            if (expirationSeconds > 0) return expirationSeconds;
            return 7 * 24 * 60 * 60;
        }
    }

    @Getter
    @Setter
    public static class Cookie {
        private String name;
        private String path;
        private String sameSite;
        private boolean secure;
    }
}