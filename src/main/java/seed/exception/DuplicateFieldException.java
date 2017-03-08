package seed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Macsnow on 2017/3/8.
 */

@ResponseStatus(HttpStatus.CONFLICT)
public class DuplicateFieldException extends RuntimeException {
    public DuplicateFieldException(String key, String value) {
        super("value \"" + value + "\" of field " + key + " is Duplicate");
    }
}
