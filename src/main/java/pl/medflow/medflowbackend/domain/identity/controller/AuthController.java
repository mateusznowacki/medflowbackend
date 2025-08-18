// src/main/java/pl/medflow/medflowbackend/auth/AuthController.java
package pl.medflow.medflowbackend.domain.auth;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import pl.medflow.medflowbackend.domain.auth.dto.LoginRequest;
import pl.medflow.medflowbackend.domain.auth.dto.LoginResponse;
import pl.medflow.medflowbackend.domain.security.JwtProperties;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final JwtProperties props;

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest request) {
        var result = authService.login(request);
        return ResponseEntity.ok()
                .header("Set-Cookie", result.refreshCookie().toString())
                .body(result.body());
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(HttpServletRequest req) {
        String cookieName = props.getCookie().getName();
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
        String cookieName = props.getCookie().getName();
        String token = null;
        if (req.getCookies() != null) {
            for (var c : req.getCookies()) {
                if (cookieName.equals(c.getName())) {
                    token = c.getValue();
                    break;
                }
            }
        }
        var clear = authService.logout(token);
        return ResponseEntity.noContent()
                .header("Set-Cookie", clear.toString())
                .build();
    }
}
