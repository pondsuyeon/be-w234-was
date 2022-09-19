package webserver;

import java.util.Map;
import java.util.Objects;

public class HttpRequest {

    private Method method;
    private String path;
    private String protocol;
    private Map<String, String> parameters;
    private Map<String, String> headers;

    public HttpRequest(Method method, String path, String protocol, Map<String, String> parameters, Map<String, String> headers) {
        this.method = method;
        this.path = path;
        this.protocol = protocol;
        this.parameters = parameters;
        this.headers = headers;
    }

    public Method getMethod() {
        return method;
    }

    public String getPath() {
        return path;
    }

    public String getProtocol() {
        return protocol;
    }

    public Map<String, String> getParameters() {
        return parameters;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        HttpRequest that = (HttpRequest) o;
        return method == that.method && path.equals(that.path) && protocol.equals( that.protocol) && parameters.equals(that.parameters);
    }

    @Override
    public int hashCode() {
        return Objects.hash(method, path, protocol, parameters);
    }
}
