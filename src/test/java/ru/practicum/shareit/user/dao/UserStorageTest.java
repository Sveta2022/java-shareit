package ru.practicum.shareit.user.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;

import static org.junit.jupiter.api.Assertions.assertNotNull;


@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class UserStorageTest {

    @Autowired
    private UserStorage userStorage;
    private final TestEntityManager em;

    private User user1;
    private User user2;

    @Autowired
    public UserStorageTest(UserStorage userStorage, TestEntityManager em) {
        this.userStorage = userStorage;
        this.em = em;
    }

    @BeforeEach
    void start() {
        user1 = new User(1L, "user1", "user1@email.ru");
        user2 = new User(2L, "user2", "user2@email.ru");

        userStorage.save(user1);
    }

    @Test
    void create() {
        TypedQuery<User> query = em.getEntityManager()
                .createQuery("Select u from User u where u.email = :email", User.class);
        User user = query.setParameter("email", user1.getEmail()).getSingleResult();
        assertNotNull(user);
    }

    @Test
    void getById() {
        TypedQuery<User> query = em.getEntityManager()
                .createQuery("Select u from User u where u.id = :userId", User.class);
        User user = query.setParameter("userId", user1.getId()).getSingleResult();
        assertNotNull(user);
    }

}