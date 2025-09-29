package pl.medflow.medflowbackend.domain.auth.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class MissingRefreshTokenException extends RuntimeException {
    public MissingRefreshTokenException() {
        super("Missing refresh token");
    }
    public MissingRefreshTokenException(String message) {
        super(message);
    }
}
