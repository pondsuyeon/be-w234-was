package webserver;

import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;


public class UserController {

    private static final Logger logger = LoggerFactory.getLogger(UserController.class);
    private static UserController instance = new UserController();

    private UserController(){
    }

    public static UserController getInstance(){
        return instance;
    }

    public void createUserWithGet(HttpRequest httpRequest){

        String userId = httpRequest.getParameters().get("userId");
        String password = httpRequest.getParameters().get("password");
        String name = httpRequest.getParameters().get("name");
        String email = httpRequest.getParameters().get("email");

        User user = new User(userId, password, name, email);

        logger.debug("CreateUserRequest Method: {}, UserInfo: {}", httpRequest.getMethod(), user);
    }

    public void createUserWithPost(HttpRequest httpRequest){

        String userId = httpRequest.getBody().get("userId");
        String password = httpRequest.getBody().get("password");
        String name = httpRequest.getBody().get("name");
        String email = httpRequest.getBody().get("email");

        User user = new User(userId, password, name, email);

        logger.debug("CreateUserRequest Method: {}, UserInfo: {}", httpRequest.getMethod(), user);
    }
}
