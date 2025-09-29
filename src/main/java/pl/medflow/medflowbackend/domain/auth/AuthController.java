package pl.medflow.medflowbackend.domain.auth;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = authService.login(request);
        return ResponseEntity.ok()
                .header("Set-Cookie", result.refreshCookie().toString())
                .body(result.body());
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest req) {
        String cookieName = authService.getCookieName();
        String token = null;
        if (req.getCookies() != null) {
            for (var c : req.getCookies()) {
                if (cookieName.equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }
        var result = authService.refresh(token);
        return ResponseEntity.ok()
                .header("Set-Cookie", result.refreshCookie().toString())
                .body(result.body());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        String cookieName = authService.getCookieName();
        String token = req.getCookies() == null ? null :
                Arrays.stream(req.getCookies())
                        .filter(c -> cookieName.equals(c.getName()))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);

        var clear = authService.logout();
        return ResponseEntity.noContent()
                .header("Set-Cookie", clear.toString())
                .build();
    }
}
