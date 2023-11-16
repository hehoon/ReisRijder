package reisrijder.reisrijder.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

public class TooManyRequestsException extends ResponseStatusException {
    public TooManyRequestsException(String reason) {
        super(HttpStatus.TOO_MANY_REQUESTS, reason);
    }
}
