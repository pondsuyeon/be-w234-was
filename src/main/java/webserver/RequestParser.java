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

    private static Method method;
    private static String path;
    private static String protocol;

    private static Map<String, String> parameters;
    private static Map<String, String> headers;
    private static Map<String, String> body;

    private static String startLine;
    private static List<String> headerLines;
    private static String bodyData;

    private static String rawLine;

    private RequestParser(){

    }
    private static void init(){
        method = null;
        path = null;
        protocol = null;

        parameters = new HashMap<>();
        headers = new HashMap<>();
        body = new HashMap<>();

        startLine = null;
        headerLines = new ArrayList<>();
        bodyData = null;

        rawLine = null;
    }

    public static HttpRequest getHttpRequestFromInput(BufferedReader br) throws IOException {
        /*
        Http의 Header 는
        `Get /login.html?loginId=abc&password=123 HTTP/1.1
        Host: localhost:8080
        Connection: keep-alive
        Accept: *

        data=hello
        `

        이러한 형식을 띕니다. 따라서
        첫 번째 라인에서 request의 method, 요청 url(path와 parameter), 프로토콜로 이루어져 있습니다.
        두 번째 라인부터 EOF가 나올 때까지 Header 속성 값들이 Key: Value 형식으로 띄고 있으며
        EOF 뒤에는 body 담긴 데이터를 확인할 수 있습니다.

         */

        init();

        // 첫 번째 라인은 3 개의 단어로 나누어 각 변수에 저장합니다.
        startLine = br.readLine();

        if (Strings.isNullOrEmpty(startLine))
            throw new RequestHandlingException("Header 입력이 비어있습니다.");

        String[] tokens = startLine.split(" ");

        if (tokens.length != 3)
            throw new RequestHandlingException("Header 입력이 잘못되었습니다.");

        method = Method.valueOf(tokens[0]);

        // url /index.html?loginId=abc&password=123에서 path는 /index.html로, ? 다음의 값들은 각각 key와 value 파싱해 map에 저장합니다.
        String[] urlTokens = tokens[1].split("\\?");

        path = urlTokens[0];

        if (urlTokens.length > 1)
            parameters = HttpRequestUtils.parseQueryString(URLDecoder.decode(urlTokens[1], StandardCharsets.UTF_8));

        protocol = tokens[2];

        // 헤더 부분은 key: value형식으로 되어있는 부분을 줄로 읽어온 다음, 각각 map에 저장합니다.
        while (true) {
            rawLine = br.readLine();
            if (rawLine == null || "".equals(rawLine)) break;
            headerLines.add(rawLine);
        }

        headerLines.forEach(line -> {
            HttpRequestUtils.Pair pair = HttpRequestUtils.parseHeader(line);
            headers.put(pair.getKey(), URLDecoder.decode(pair.getValue(), StandardCharsets.UTF_8));
        });

        // body는 없는 경우가 존재하기 때문에 있는 경우, 없는 경우를 try-catch를 사용해 저장합니다. 단 이때, 특수문자 등의 문제로 decoding이 필요하므로 처리해줍니다.
        try {
            bodyData = IOUtils.readData(br, Integer.parseInt(headers.get("Content-Length")));
            body = HttpRequestUtils.parseQueryString(URLDecoder.decode(bodyData, StandardCharsets.UTF_8));

        } catch (Exception e) {

        }

        return new HttpRequest(method, path, protocol, parameters, headers, body);
    }
}
