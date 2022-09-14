package webserver;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashMap;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

class RequestParserTest {

    @Test
    @DisplayName("Header의 시작줄 파싱 정상 작동을 테스트한다.")
    void parsingStartLineTest(){

        String startLine = "GET /user/create?userId=lucy.ji&password=1234&name=lucy&email=lucy.ji@kakaocorp.com HTTP/1.1";

        HttpRequest actualRequest = RequestParser.getHttpRequestFromInput(startLine);
        HttpRequest expectedRequest = initExpectedRequest();

        assertThat(actualRequest)
                .isEqualTo(expectedRequest);
    }

    private HttpRequest initExpectedRequest() {
        Map<String, String> parameters = new HashMap<>();
        parameters.put("userId", "lucy.ji");
        parameters.put("password", "1234");
        parameters.put("name", "lucy");
        parameters.put("email", "lucy.ji@kakaocorp.com");

        return new HttpRequest(Method.GET, "/user/create", "HTTP/1.1", parameters);
    }
}