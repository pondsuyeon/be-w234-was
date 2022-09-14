package webserver;

import com.google.common.base.Strings;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.Map;

public class RequestParser {

    public static HttpRequest getHttpRequestFromInput(String line) {

        Method method = null;
        String path = null;
        String protocol = null;
        Map<String, String> parameters = new HashMap<>();

        if (Strings.isNullOrEmpty(line))
            throw new RequestHandlingException("Header 입력이 비어있습니다.");

        String[] tokens = line.split(" ");

        if (tokens.length != 3)
            throw new RequestHandlingException("Header 입력이 잘못되었습니다.");

        method = Method.valueOf(tokens[0]);

        String[] urlTokens = tokens[1].split("\\?");

        path = urlTokens[0];

        if (urlTokens.length > 1)
            parameters = HttpRequestUtils.parseQueryString(urlTokens[1]);

        protocol = tokens[2];

        return new HttpRequest(method, path, protocol, parameters);
    }
}
