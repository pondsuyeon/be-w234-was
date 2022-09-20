package controller;

import exception.LoginFailException;
import exception.UserNotFoundException;
import model.User;
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
    private static UserController instance = new UserController();

    private UserController() {
    }

    public static UserController getInstance() {
        return instance;
    }

    public HttpResponse process(HttpRequest httpRequest) throws IOException {
        HttpResponse httpResponse = null;
        if (httpRequest.getMethod() == Method.POST && httpRequest.getPath().equals("/user/create")) {
            httpResponse = createUserWithPost(httpRequest);
        } else if (httpRequest.getMethod() == Method.POST && httpRequest.getPath().equals("/user/login")) {
            httpResponse = login(httpRequest);
        }

        return httpResponse;
    }

    public HttpResponse createUserWithPost(HttpRequest httpRequest) {

        String userId = httpRequest.getBody().get("userId");
        String password = httpRequest.getBody().get("password");
        String name = httpRequest.getBody().get("name");
        String email = httpRequest.getBody().get("email");

        UserService userService = UserService.getInstance();

        User user = userService.createUser(userId, password, name, email);

        logger.debug("CreateUserRequest Method: {}, UserInfo: {}", httpRequest.getMethod(), user);

        return new HttpResponse.Builder()
                .statusCode(StatusCode.FOUND)
                .headers("Location", "http://" + httpRequest.getHeaders().get("Host") + "/index.html")
                .mime(httpRequest.getHeaders().get("Accept"))
                .build();
    }

    public HttpResponse login(HttpRequest httpRequest) throws IOException {

        String userId = httpRequest.getBody().get("userId");
        String password = httpRequest.getBody().get("password");

        UserService userService = UserService.getInstance();

        try {
            userService.login(userId, password);
        } catch (UserNotFoundException | LoginFailException e) {
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
}
