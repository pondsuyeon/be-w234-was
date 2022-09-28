package repository;

import dto.PostForm;
import model.Post;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class PostRepository {

    private static final PostRepository instance = new PostRepository();

    private PostRepository() {
    }

    public static PostRepository getInstance() {
        return instance;
    }

    public void createPost(PostForm postForm, User writer) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("was");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            Post post = Post.builder()
                    .writer(writer)
                    .content(postForm.getContent())
                    .build();

            em.persist(post);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();

    }

    public List<Post> findAll() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("was");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        List<Post> postList = new ArrayList<>();
        try {
            tx.begin();
            postList = em.createQuery("select p from Post p order by p.createdAt desc", Post.class)
                    .getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
        return postList;

    }
}
