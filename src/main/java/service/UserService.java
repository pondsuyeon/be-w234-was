package service;

import db.Database;
import dto.JoinUserDto;
import dto.JoinUserDto;
import dto.LoginUserDto;
import exception.DuplicateUserException;
import exception.LoginFailException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static UserService instance = new UserService();

    private UserService() {

    }

    public static UserService getInstance() {
        return instance;
    }

    public User createUser(JoinUserDto joinUserDto) {

        if (Database.findUserById(joinUserDto.getUserId()) != null) {
            throw new DuplicateUserException("ID가 중복되었습니다.");
        }

        User user = new User.Builder()
                .userId(joinUserDto.getUserId())
                .password(joinUserDto.getPassword())
                .name(joinUserDto.getName())
                .email(joinUserDto.getEmail())
                .build();

        Database.addUser(user);

        return user;
    }

    public boolean checkUserPassword(LoginUserDto loginUserDto) {

        User savedUser = Database.findUserById(loginUserDto.getUserId());

        if (savedUser == null || !savedUser.getPassword().equals(loginUserDto.getPassword())) {
            throw new LoginFailException("Id와 Password를 다시 확인해주세요.");
        }

        return true;
    }

    public void deleteUser(String userId) {

        Database.deleteUser(userId);
    }

    public List<User> getUserList() {
        return new ArrayList<>(Database.findAll());
    }

}
