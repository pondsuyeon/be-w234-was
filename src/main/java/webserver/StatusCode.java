package webserver;

public enum StatusCode {

    OK(200, "OK"),

    FOUND(302, "FOUND"),

    NOT_FOUND(404, "NOT_FOUND");

    private final int status;
    private final String message;

    StatusCode(int status, String message) {
        this.status = status;
        this.message = message;
    }

    public int getStatus() {
        return status;
    }

    public String getMessage() {
        return message;
    }
}
