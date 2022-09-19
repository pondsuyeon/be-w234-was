package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final String protocol = "HTTP/1.1";
    private StatusCode statusCode;
    private Map<String, String> headers;
    private byte[] body = new byte[0];
    private String redirectUrl;

    public HttpResponse(StatusCode statusCode, String requestHeaderAccept) {
        this.statusCode = statusCode;
        this.headers = new HashMap<>();
        this.headers.put("Content-Type", getContentTypeFromRequest(requestHeaderAccept));
    }



    public HttpResponse(StatusCode statusCode, String redirectUrl, String requestHeaderAccept) {
        this(statusCode, requestHeaderAccept);
        this.redirectUrl = redirectUrl;
    }
    public HttpResponse(StatusCode statusCode, byte[] body, String requestHeaderAccept) {
        this(statusCode, requestHeaderAccept);
        this.body = body;
    }

    public String getProtocol() {
        return protocol;
    }

    public StatusCode getStatusCode() {
        return statusCode;
    }

    public byte[] getBody() {
        return body;
    }

    public Map<String, String> getHeaders() {
        return headers;
    }

    public String getRedirectUrl() {
        return redirectUrl;
    }
    private String getContentTypeFromRequest(String requestHeaderAccept) {
        if (requestHeaderAccept.contains("text/css")) {
            return "text/css";
        }
        return "text/html";
    }
}