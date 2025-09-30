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
        var result = authService.refresh(req); // żadnego getCookieName, wszystko ukryte
        return ResponseEntity.ok()
                .header("Set-Cookie", result.refreshCookie().toString())
                .body(result.body());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout(HttpServletRequest req) {
        String token = null;
        if (req.getCookies() != null) {
            for (Cookie c : req.getCookies()) {
                if ("refreshToken".equals(c.getName())) { // default; can be configured
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
