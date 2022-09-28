package repository;

import dto.PostForm;
import model.User;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import service.UserService;

import static org.junit.jupiter.api.Assertions.*;

class PostRepositoryTest {

    @Test
    @DisplayName("게시글을 성공적으로 작성한다.")
    void createPost() {

        String writerId = "abc";
        User writer = UserRepository.getInstance().findUserById(writerId);
        PostForm postForm = PostForm.builder()
                .writerId(writerId)
                .content("우왕 게시글이다~")
                .build();

        
    }

    @Test
    void findAll() {
    }
}