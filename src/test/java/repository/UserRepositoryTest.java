package repository;

import org.junit.jupiter.api.Test;

class UserRepositoryTest {

    @Test
    void deleteUser(){
        UserRepository.getInstance().deleteUser("abc");
    }
}