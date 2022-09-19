package webserver;

public class RequestHandlingException extends RuntimeException{

    public RequestHandlingException(String message) {
        super(message);
    }
}
