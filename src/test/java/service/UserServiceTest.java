package service;

import dto.JoinUserDto;
import dto.LoginUserDto;
import exception.DuplicateUserException;
import exception.LoginFailException;
import model.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class UserServiceTest {

    @BeforeEach
    void deleteUser(){
            UserService.getInstance().deleteUser("abc");
            UserService.getInstance().deleteUser("cde");
    }

    @Test
    @DisplayName("사용자 회원가입 정상 테스트를 진행한다.")
    void createUser(){
        String userId = "abc";
        String password = "123";
        String name = "hello_abc";
        String email = "abc@abc.com";

        UserService.getInstance().createUser(new JoinUserDto(userId, password, name, email));
    }

    @Test
    @DisplayName("사용자 회원가입 중복 ID 테스트, ID가 중복일 경우 DuplicateUserException을 반환한다.")
    void createUserWithDuplicatedId(){

        createUserBeforeTest();

        String userId = "abc";
        String password = "123";
        String name = "hello_abc";
        String email = "abc@abc.com";

        assertThrows(DuplicateUserException.class, () -> UserService.getInstance().createUser(new JoinUserDto(userId, password, name, email)));

    }

    @Test
    @DisplayName("로그인 성공 테스트")
    void login() {
        createUserBeforeTest();

        String userId = "abc";
        String password = "123";

        UserService.getInstance().checkUserPassword(new LoginUserDto(userId, password));
    }

    @Test
    @DisplayName("로그인 ID 실패 테스트, 등록된 ID가 없는 경우 LoginFailException을 반환한다")
    void loginWithNoId() {

        String userId = "abc";
        String password = "123";

        assertThrows(LoginFailException.class, () ->
        UserService.getInstance().checkUserPassword(new LoginUserDto(userId, password)));
    }

    @Test
    @DisplayName("로그인 불일치 테스트, 등록된 ID와 PW가 불일치하는 경우 LoginFailException을 반환한다")
    void loginNoMatched() {

        createUserBeforeTest();

        String userId = "abc";
        String password = "456";

        assertThrows(LoginFailException.class, () ->
                UserService.getInstance().checkUserPassword(new LoginUserDto(userId, password)));
    }

    @Test
    @DisplayName("가입한 사용자 목록 테스트")
    void userAllList(){

        createUserBeforeTest();

        List<User> userList = UserService.getInstance().getUserList();
        assertEquals(userList.size(), 2);
        assertTrue(userList.stream().anyMatch(user -> user.getUserId().equals("abc")));
        assertTrue(userList.stream().anyMatch(user -> user.getUserId().equals("cde")));

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