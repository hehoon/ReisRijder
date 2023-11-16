package reisrijder.reisrijder.exceptions;

import org.springframework.web.server.ResponseStatusException;

public class ExceptionHandler {

    public static void handleException(Exception e) throws ResponseStatusException {
        if (e instanceof ResponseStatusException) {
            throw (ResponseStatusException) e;
        }

        e.printStackTrace();
        throw new InternalServerErrorException("Oops! Something went wrong");
    }

}
