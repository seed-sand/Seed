package seed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Froggy
 * 2017-03-02.
 */
@ResponseStatus(HttpStatus.CONFLICT)
public class IncorrectPasswordException extends RuntimeException {
    public IncorrectPasswordException(){
        super("Your login password is incorrect.");
    }
}
