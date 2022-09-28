package service;

import repository.UserRepository;
import dto.JoinUserDto;
import dto.LoginUserDto;
import exception.DuplicateUserException;
import exception.LoginFailException;
import model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;

public class UserService {
    private static final Logger logger = LoggerFactory.getLogger(UserService.class);
    private static final UserService instance = new UserService();

    private static final UserRepository userRepository = UserRepository.getInstance();
    private UserService() {

    }

    public static UserService getInstance() {
        return instance;
    }

    public void createUser(JoinUserDto joinUserDto) {

        if (userRepository.findUserById(joinUserDto.getUserId()) != null) {
            throw new DuplicateUserException("ID가 중복되었습니다.");
        }

        userRepository.addUser(joinUserDto);

    }

    public boolean checkUserPassword(LoginUserDto loginUserDto) {

        User savedUser = userRepository.findUserById(loginUserDto.getUserId());

        if (savedUser == null || !savedUser.getPassword().equals(loginUserDto.getPassword())) {
            throw new LoginFailException("Id와 Password를 다시 확인해주세요.");
        }

        return true;
    }

    public void deleteUser(String userId) {

        userRepository.deleteUser(userId);
    }

    public List<User> getUserList() {
        return userRepository.findAll();
    }

    public User getUserByUserId(String userId) {
        return userRepository.findUserById(userId);
    }
}
