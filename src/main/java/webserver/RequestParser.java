package webserver;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    public static HttpRequest getHttpRequestFromInput(String startLine, List<String> headerLines) {

        Method method = null;
        String path = null;
        String protocol = null;
        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();

        if (Strings.isNullOrEmpty(startLine))
            throw new RequestHandlingException("Header 입력이 비어있습니다.");

        String[] tokens = startLine.split(" ");

        if (tokens.length != 3)
            throw new RequestHandlingException("Header 입력이 잘못되었습니다.");

        method = Method.valueOf(tokens[0]);

        String[] urlTokens = tokens[1].split("\\?");

        path = urlTokens[0];

        if (urlTokens.length > 1)
            parameters = HttpRequestUtils.parseQueryString(urlTokens[1]);

        protocol = tokens[2];

        headerLines.forEach(line -> {
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), pair.getValue());
        });

        return new HttpRequest(method, path, protocol, parameters, headers);
    }
}
