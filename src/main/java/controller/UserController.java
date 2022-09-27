package controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import dto.JoinUserDto;
import dto.LoginUserDto;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import service.UserService;
import webserver.HttpRequest;
import webserver.HttpResponse;
import webserver.Method;
import webserver.StatusCode;

import java.io.IOException;

public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static final UserController instance = new UserController();
    private static final ObjectMapper objectMapper = new ObjectMapper();

    private UserController() {
    }

    public static UserController getInstance() {
        return instance;
    }

    public HttpResponse process(HttpRequest httpRequest) throws IOException {

        HttpResponse httpResponse = null;
        Method method = httpRequest.getMethod();

        switch (method) {

            case GET: {
                httpResponse = processGetRequest(httpRequest);
                break;
            }
            case POST: {
                httpResponse = processPostRequest(httpRequest);
                break;
            }

            default:
                httpResponse = null;
        }

        return httpResponse;
    }
    private HttpResponse processGetRequest(HttpRequest httpRequest) throws IOException {

        HttpResponse httpResponse = null;
        String path = httpRequest.getPath().replaceFirst("/user", "");

        switch (path){
            case "/list": {
                httpResponse = getAllUserList(httpRequest);
                break;
            }
            default:
                httpResponse = null;
        }

        return httpResponse;
    }

    private HttpResponse processPostRequest(HttpRequest httpRequest) {

        HttpResponse httpResponse = null;
        String path = httpRequest.getPath().replaceFirst("/user", "");

        switch (path) {
            case "/create": {
                httpResponse = createUser(httpRequest);
                break;
            }
            case "/login": {
                httpResponse = login(httpRequest);
                break;
            }
            default:
                httpResponse = null;
        }

        return httpResponse;
    }

    public HttpResponse createUser(HttpRequest httpRequest) {

        JoinUserDto joinUserDto = objectMapper.convertValue(httpRequest.getBody(), JoinUserDto.class);

        UserService userService = UserService.getInstance();

        userService.createUser(joinUserDto);

        return new HttpResponse.Builder()
                .statusCode(StatusCode.FOUND)
                .headers("Location", "http://" + httpRequest.getHeaders().get("Host") + "/index.html")
                .mime(httpRequest.getHeaders().get("Accept"))
                .build();
    }

    public HttpResponse login(HttpRequest httpRequest) {

        LoginUserDto loginUserDto = objectMapper.convertValue(httpRequest.getBody(), LoginUserDto.class);

        UserService userService = UserService.getInstance();

        try {
            userService.checkUserPassword(loginUserDto);
        } catch (Exception e) {
            return new HttpResponse.Builder()
                    .statusCode(StatusCode.FOUND)
                    .headers("Location", "http://" + httpRequest.getHeaders().get("Host") + "/user/login_failed.html")
                    .headers("Set-Cookie", "logined=false; Path=/")
                    .build();
        }

        return new HttpResponse.Builder()
                .statusCode(StatusCode.FOUND)
                .headers("Location", "http://" + httpRequest.getHeaders().get("Host") + "/index.html")
                .headers("Set-Cookie", "logined=true; Path=/")
                .build();
    }

    public HttpResponse getAllUserList(HttpRequest httpRequest) throws IOException {

        if (!httpRequest.getHeaders().containsKey("Cookie") || !httpRequest.getHeaders().get("Cookie").contains("logined=true")) {
            return new HttpResponse.Builder()
                    .statusCode(StatusCode.FOUND)
                    .headers("Location", "http://" + httpRequest.getHeaders().get("Host") + "/user/login.html")
                    .build();
        }

        return new HttpResponse.Builder()
                .statusCode(StatusCode.OK)
                .body(objectMapper.writeValueAsString(UserService.getInstance().getUserList()).getBytes())
                .build();

    }

}
