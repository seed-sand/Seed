package seed.exception;

import org.bson.types.ObjectId;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

/**
 * Created by Macsnow on 2017/3/1.
 */

@ResponseStatus(HttpStatus.NOT_FOUND)
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(ObjectId resourceId, String resource) {
        super("could not find "+ resource +" '" + resourceId + "'.");
    }
    public ResourceNotFoundException(String resourceIdentity, String resource) {
        super("could not find "+ resource +" '" + resourceIdentity + "'.");
    }
    public ResourceNotFoundException(String resource) {
        super("could not find "+ resource +".");
    }
}
