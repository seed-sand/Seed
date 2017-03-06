package seed.exception;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Macsnow on 2017/3/6.
 */
@ResponseStatus(HttpStatus.BAD_REQUEST)
public class InvalidFieldException extends RuntimeException {
    public InvalidFieldException(String field){
        super("Field" + field + " in query is invalid.");
    }
}
