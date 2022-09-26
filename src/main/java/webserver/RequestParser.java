package webserver;

import com.google.common.base.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import util.HttpRequestUtils;
import util.IOUtils;

import java.io.BufferedReader;
import java.io.IOException;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class RequestParser {
    private static final Logger logger = LoggerFactory.getLogger(RequestParser.class);
    public static HttpRequest getHttpRequestFromInput(BufferedReader br) throws IOException {

        Method method = null;
        String path = null;
        String protocol = null;

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        String startLine = null;
        List<String> headerLines = new ArrayList<>();
        String bodyData = null;

        String rawLine = null;

        startLine = br.readLine();

        while (true) {
            rawLine = br.readLine();
            if (rawLine == null || "".equals(rawLine)) break;
            headerLines.add(rawLine);
        }

        if (Strings.isNullOrEmpty(startLine))
            throw new RequestHandlingException("Header 입력이 비어있습니다.");

        String[] tokens = startLine.split(" ");

        if (tokens.length != 3)
            throw new RequestHandlingException("Header 입력이 잘못되었습니다.");

        method = Method.valueOf(tokens[0]);

        String[] urlTokens = tokens[1].split("\\?");

        path = urlTokens[0];

        if (urlTokens.length > 1)
            parameters = HttpRequestUtils.parseQueryString(URLDecoder.decode(urlTokens[1], StandardCharsets.UTF_8));

        protocol = tokens[2];

        headerLines.forEach(line -> {
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), URLDecoder.decode(pair.getValue(), StandardCharsets.UTF_8));
        });

        try {
            bodyData = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
            body = HttpRequestUtils.parseQueryString(URLDecoder.decode(bodyData, StandardCharsets.UTF_8));

        } catch (Exception e) {

        }

        return new HttpRequest(method, path, protocol, parameters, headers, body);
    }
}
