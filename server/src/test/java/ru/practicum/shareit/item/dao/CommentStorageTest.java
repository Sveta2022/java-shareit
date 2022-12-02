package ru.practicum.shareit.item.dao;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.autoconfigure.orm.jpa.TestEntityManager;
import org.springframework.test.annotation.DirtiesContext;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.item.model.Item;
import ru.practicum.shareit.user.dao.UserStorage;
import ru.practicum.shareit.user.model.User;

import javax.persistence.TypedQuery;
import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.*;

@DataJpaTest
@DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class CommentStorageTest {

    private CommentStorage commentStorage;
    private ItemStorage itemStorage;
    private UserStorage userStorage;
    private final TestEntityManager em;
    private Comment comment1;

    private Item item1;
    private User user1;

    @Autowired
    public CommentStorageTest(CommentStorage commentStorage, ItemStorage itemStorage, UserStorage userStorage, TestEntityManager em) {
        this.commentStorage = commentStorage;
        this.itemStorage = itemStorage;
        this.userStorage = userStorage;
        this.em = em;
    }

    @BeforeEach
    void start() {
        user1 = new User(1L, "user1", "user1@email.ru");
        item1 = new Item(1L, "itemName1", "descriptionItem1", user1, true, null);
        comment1 = new Comment(1L, "textComment1", item1, user1, LocalDateTime.now());
        userStorage.save(user1);
        itemStorage.save(item1);
        commentStorage.save(comment1);
    }

    @Test
    void findAllByItemId() {
        TypedQuery<Comment> query = em.getEntityManager()
                .createQuery("Select c from Comment c join Item i on c.item.id = i.id where c.item.id = :itemId", Comment.class);
        Comment comment = query.setParameter("itemId", comment1.getId()).getSingleResult();
        assertNotNull(comment);
    }
}