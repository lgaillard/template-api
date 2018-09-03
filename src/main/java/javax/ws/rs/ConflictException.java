package javax.ws.rs;

import javax.ws.rs.core.Response;

public class ConflictException extends ClientErrorException {

    public ConflictException() {
        super(Response.Status.CONFLICT);
    }

    public ConflictException(String message) {
        super(message, Response.Status.CONFLICT);
    }

    public ConflictException(Response response) {
        super(validate(response, Response.Status.CONFLICT));
    }

    public ConflictException(String message, Response response) {
        super(message, validate(response, Response.Status.CONFLICT));
    }

    public ConflictException(Throwable cause) {
        super(Response.Status.CONFLICT, cause);
    }

    public ConflictException(String message, Throwable cause) {
        super(message, Response.Status.CONFLICT, cause);
    }

    public ConflictException(Response response, Throwable cause) {
        super(validate(response, Response.Status.CONFLICT), cause);
    }

    public ConflictException(String message, Response response, Throwable cause) {
        super(message, validate(response, Response.Status.CONFLICT), cause);
    }
}
