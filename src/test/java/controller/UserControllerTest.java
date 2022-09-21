package controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.UserService;
import webserver.HttpRequest;
import webserver.Method;

import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;

class UserControllerTest {

    @BeforeEach
    void deleteUser(){
        try {
            UserService.getInstance().deleteUser("abc");
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

        HttpRequest httpRequest = new HttpRequest(method, path, protocol, parameters, headers, body);

        UserController.getInstance().createUserWithPost(httpRequest);
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
    void createUserBeforeTest(){

        String userId = "abc";
        String password = "123";
        String name = "hello_abc";
        String email = "abc@abc.com";

        UserService.getInstance().createUser(userId, password, name, email);
    }
}