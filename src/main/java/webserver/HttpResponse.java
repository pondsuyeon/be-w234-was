package webserver;

import java.util.HashMap;
import java.util.Map;

public class HttpResponse {
    private final String protocol = "HTTP/1.1";
    private StatusCode statusCode;
    private Map<String, String> headers;
    private byte[] body;

    private HttpResponse(Builder builder) {
        this.statusCode = builder.statusCode;
        this.headers = builder.headers;
        this.body = builder.body;
    }

    public static class Builder {
        private StatusCode statusCode;
        private Map<String, String> headers = new HashMap<>();
        private byte[] body = new byte[0];

        public Builder() {
            this.headers.put("Content-Type", getContentTypeFromRequest(""));
        }

        public Builder statusCode(StatusCode statusCode) {
            this.statusCode = statusCode;
            return this;
        }

        public Builder headers(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        public Builder body(byte[] body) {
            this.body = body;
            this.headers.put("Content-Length", String.valueOf(body.length));
            return this;
        }

        public Builder mime(String requestHeaderAccept) {
            this.headers.put("Content-Type", getContentTypeFromRequest(requestHeaderAccept) + ";charset=utf-8");
            return this;
        }

        public HttpResponse build() {
            return new HttpResponse(this);
        }

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

    private static String getContentTypeFromRequest(String requestHeaderAccept) {
        if (requestHeaderAccept == null || "".equals(requestHeaderAccept)){
            return "text/html";
        }

        if (requestHeaderAccept.contains("text/css")) {
            return "text/css";
        }
        return "text/html";
    }
}