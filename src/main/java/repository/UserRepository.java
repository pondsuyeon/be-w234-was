package repository;

import dto.JoinUserDto;
import model.User;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.EntityTransaction;
import javax.persistence.Persistence;
import java.util.ArrayList;
import java.util.List;

public class UserRepository {

    private static final UserRepository instance = new UserRepository();

    private UserRepository() {
    }

    public static UserRepository getInstance() {
        return instance;
    }

    public void addUser(JoinUserDto joinUserDto) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("was");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();

            User user = User.builder()
                    .userId(joinUserDto.getUserId())
                    .password(joinUserDto.getPassword())
                    .name(joinUserDto.getName())
                    .email(joinUserDto.getEmail())
                    .build();

            em.persist(user);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    public User findUserById(String userId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("was");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        User user = null;
        try {
            tx.begin();
            user = em.createQuery("select u from User u where u.userId=:userId", User.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
        return user;
    }

    public void deleteUser(String userId) {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("was");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        try {
            tx.begin();
            User user = em.createQuery("select u from User u where u.userId=:userId", User.class)
                    .setParameter("userId", userId)
                    .getSingleResult();

            em.remove(user);

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }
        emf.close();
    }

    public List<User> findAll() {
        EntityManagerFactory emf = Persistence.createEntityManagerFactory("was");
        EntityManager em = emf.createEntityManager();
        EntityTransaction tx = em.getTransaction();

        List<User> userList = new ArrayList<>();
        try {
            tx.begin();
            userList = em.createQuery("select u from User u", User.class)
                            .getResultList();

            tx.commit();
        } catch (Exception e) {
            tx.rollback();
        } finally {
            em.close();
        }

        emf.close();
        return userList;
    }
}
