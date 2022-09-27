package controller;

import dto.JoinUserDto;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.UserService;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.Method;
import webserver.StatusCode;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @BeforeEach
    void deleteUser(){
        try {
            UserService.getInstance().deleteUser("abc");
            UserService.getInstance().deleteUser("cde");
        } catch (Exception e){

        }
    }
    @Test
    @DisplayName("사용자 회원가입 테스트")
    void createUser(){

        Method method = Method.POST;
        String path = "/user/create";
        String protocol = "HTTP/1.1";

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("userId", "abc");
        body.put("password", "123");
        body.put("name", "hello_abc");
        body.put("email", "abc@abc.com");

        HttpRequest httpRequest = new HttpRequest(method, path, protocol, parameters, headers, body);

        UserController.getInstance().createUser(httpRequest);
    }

    @Test
    @DisplayName("사용자 로그인 정상 테스트")
    void login(){

        createUserBeforeTest();

        Method method = Method.POST;
        String path = "/user/login";
        String protocol = "HTTP/1.1";

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("userId", "abc");
        body.put("password", "123");

        HttpRequest httpRequest = new HttpRequest(method, path, protocol, parameters, headers, body);

        UserController.getInstance().login(httpRequest);

    }

    @Test
    @DisplayName("사용자 로그인 없는 정보 테스트, 헤더의 Set-Cookie 값에 logined=false이어야 한다.")
    void loginWithNoId(){

        createUserBeforeTest();

        Method method = Method.POST;
        String path = "/user/login";
        String protocol = "HTTP/1.1";

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("userId", "abcd");
        body.put("password", "123");

        HttpRequest httpRequest = new HttpRequest(method, path, protocol, parameters, headers, body);

        assertTrue(UserController.getInstance().login(httpRequest).getHeaders().get("Set-Cookie").contains("logined=false"));

    }

    @Test
    @DisplayName("사용자 로그인 실패 테스트, 헤더의 Set-Cookie 값에 logined=false이어야 한다.")
    void loginNoMatched(){

        createUserBeforeTest();

        Method method = Method.POST;
        String path = "/user/login";
        String protocol = "HTTP/1.1";

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        body.put("userId", "abc");
        body.put("password", "1234");

        HttpRequest httpRequest = new HttpRequest(method, path, protocol, parameters, headers, body);

        assertTrue(UserController.getInstance().login(httpRequest).getHeaders().get("Set-Cookie").contains("logined=false"));

    }

    @Test
    @DisplayName("로그인이 되어있을 경우 사용자 목록 반환 테스트")
    void getAllUserList() throws IOException {
        Method method = Method.GET;
        String path = "/user/list";
        String protocol = "HTTP/1.1";

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        headers.put("Cookie", "logined=true");

        HttpRequest httpRequest = new HttpRequest(method, path, protocol, parameters, headers, body);

        assertEquals(UserController.getInstance().getAllUserList(httpRequest).getStatusCode(), StatusCode.OK);

    }

    @Test
    @DisplayName("로그인이 안 되어있을 경우 사용자 목록 반환 테스트")
    void getAllUserListWithLogout() throws IOException {
        Method method = Method.GET;
        String path = "/user/list";
        String protocol = "HTTP/1.1";

        Map<String, String> parameters = new HashMap<>();
        Map<String, String> headers = new HashMap<>();
        Map<String, String> body = new HashMap<>();

        headers.put("Cookie", "logined=false");

        HttpRequest httpRequest = new HttpRequest(method, path, protocol, parameters, headers, body);
        HttpResponse httpResponse = UserController.getInstance().getAllUserList(httpRequest);

        assertEquals(httpResponse.getStatusCode(), StatusCode.FOUND);
        assertTrue(httpResponse.getHeaders().get("Location").contains("/login.html"));

    }
    void createUserBeforeTest(){

        String userId = "abc";
        String password = "123";
        String name = "hello_abc";
        String email = "abc@abc.com";

        UserService.getInstance().createUser(new JoinUserDto(userId, password, name, email));

        userId = "cde";
        password = "456";
        name = "hello_cde";
        email = "cde@cde.com";

        UserService.getInstance().createUser(new JoinUserDto(userId, password, name, email));
    }
}
