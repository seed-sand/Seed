package seed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Froggy
 * 2017-03-01.
 */

@ResponseStatus(HttpStatus.BAD_REQUEST)
public class RequiredFieldMissingException extends RuntimeException {
    public RequiredFieldMissingException(String field){
        super("Required Field " + field + " missing");
    }
}
