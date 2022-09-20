package service;

import db.Database;
import exception.LoginFailException;
import exception.UserNotFoundException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static UserService instance = new UserService();

    private UserService() {

    }

    public static UserService getInstance() {
        return instance;
    }

    public User createUser(String userId, String password, String name, String email) {

        User user = new User.Builder()
                .userId(userId)
                .password(password)
                .name(name).email(email)
                .build();

        Database.addUser(user);

        return user;
    }

    public boolean login(String userId, String password) {

        User user = Database.findUserById(userId);

        if (user == null) {
            throw new UserNotFoundException("일치하는 사용자 ID가 없습니다.");
        }

        if (!user.getPassword().equals(password)) {
            throw new LoginFailException("ID와 Password가 일치하지 않습니다.");
        }

        return true;
    }
}
