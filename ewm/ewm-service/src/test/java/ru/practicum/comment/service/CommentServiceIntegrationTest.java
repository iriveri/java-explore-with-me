package ru.practicum.comment.service;

import jakarta.transaction.Transactional;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import ru.practicum.category.service.CategoryService;
import ru.practicum.comment.BanRepository;
import ru.practicum.comment.Comment;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.CommentRepository;
import ru.practicum.dto.category.NewCategoryDto;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;
import ru.practicum.dto.event.Location;
import ru.practicum.dto.event.NewEventDto;
import ru.practicum.dto.event.admin.AdminAction;
import ru.practicum.dto.event.admin.AdminUpdateEventRequest;
import ru.practicum.dto.user.NewUserDto;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.user.service.UserService;

import java.time.LocalDateTime;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@Transactional
@AutoConfigureMockMvc
public class CommentServiceIntegrationTest {

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private BanRepository banRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private EventService eventService;
    @Autowired
    private CategoryService categoryService;

    @Autowired
    private CommentMapper commentMapper;

    private Long userId;
    private Long eventId;

    @BeforeEach
    public void setUp() {
        // Create a test user
        NewUserDto user = new NewUserDto();
        user.setName("Test User");
        user.setEmail("test@example.com");
        userId = userService.create(user).getId();
        // Create a test user
        NewCategoryDto category = new NewCategoryDto();
        category.setName("Test category");
        Long categoryId = categoryService.create(category).getId();


        // Create a test event
        NewEventDto event = new NewEventDto();
        event.setTitle("Test Event");
        event.setAnnotation("Annatationnnnnnnnnnnnnnnnnnnn");
        event.setCategory(categoryId);
        event.setDescription("Descriptionnnnnnnnnnnnnnnnnnnn");
        event.setEventDate(LocalDateTime.now().plusDays(3));
        event.setLocation(new Location(1, 1));
        eventId = eventService.create(userId, event).getId();
        eventService.update(eventId, new AdminUpdateEventRequest(AdminAction.PUBLISH_EVENT));
    }

    @Test
    public void testCreateComment() {
        NewCommentDto newCommentDto = new NewCommentDto(eventId, "Test comment");

        ShortCommentDto createdComment = commentService.create(newCommentDto, userId);

        assertThat(createdComment).isNotNull();
        assertThat(createdComment.getText()).isEqualTo("Test comment");

        // Verify the comment is stored in the repository
        Comment savedComment = commentRepository.findById(createdComment.getId()).orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Test comment");
        assertThat(savedComment.getUser().getId()).isEqualTo(userId);
        assertThat(savedComment.getEvent().getId()).isEqualTo(eventId);
    }

    @Test
    public void testCreateComment_UserBanned() {
        // Ban the user from commenting on this event
        commentService.banUserFromCommenting(userId, eventId);

        NewCommentDto newCommentDto = new NewCommentDto(eventId, "Test comment");

        ConditionNotMetException exception = assertThrows(ConditionNotMetException.class,
                () -> commentService.create(newCommentDto, userId));
        assertThat(exception.getMessage()).isEqualTo("User is banned from commenting on this event");
    }

    @Test
    public void testEditComment() {
        // Create a comment to edit
        Comment comment = new Comment();
        comment.setText("Original comment");
        comment.setUser(userService.getEntityById(userId));
        comment.setEvent(eventService.getEntityById(eventId));
        comment.setCreatedAt(LocalDateTime.now());
        Long commentId = commentRepository.save(comment).getId();

        // Edit the comment
        ShortCommentDto updatedComment = commentService.edit(commentId, userId, "Updated comment");

        assertThat(updatedComment).isNotNull();
        assertThat(updatedComment.getText()).isEqualTo("Updated comment");

        // Verify the comment is updated in the repository
        Comment savedComment = commentRepository.findById(commentId).orElse(null);
        assertThat(savedComment).isNotNull();
        assertThat(savedComment.getText()).isEqualTo("Updated comment");
    }

    @Test
    public void testEditComment_OtherUser() {
        // Create another user
        NewUserDto otherUser = new NewUserDto();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        Long otherUserId = userService.create(otherUser).getId();

        // Create a comment by the other user
        Comment comment = new Comment();
        comment.setText("Original comment");
        comment.setUser(userService.getEntityById(otherUserId));
        comment.setEvent(eventService.getEntityById(eventId));
        comment.setCreatedAt(LocalDateTime.now());
        Long commentId = commentRepository.save(comment).getId();

        // Attempt to edit the comment as the original user
        ConditionNotMetException exception = assertThrows(ConditionNotMetException.class,
                () -> commentService.edit(commentId, userId, "Updated comment"));
        assertThat(exception.getMessage()).isEqualTo("You can only edit your own comments");
    }

    @Test
    public void testDeleteComment() {
        // Create a comment to delete
        Comment comment = new Comment();
        comment.setText("Comment to delete");
        comment.setUser(userService.getEntityById(userId));
        comment.setEvent(eventService.getEntityById(eventId));
        comment.setCreatedAt(LocalDateTime.now());
        Long commentId = commentRepository.save(comment).getId();

        // Delete the comment
        commentService.delete(commentId, userId);

        // Verify the comment is deleted from the repository
        assertFalse(commentRepository.findById(commentId).isPresent());
    }

    @Test
    public void testDeleteComment_OtherUser() {
        // Create another user
        NewUserDto otherUser = new NewUserDto();
        otherUser.setName("Other User");
        otherUser.setEmail("other@example.com");
        Long otherUserId = userService.create(otherUser).getId();

        // Create a comment by the other user
        Comment comment = new Comment();
        comment.setText("Comment to delete");
        comment.setUser(userService.getEntityById(otherUserId));
        comment.setEvent(eventService.getEntityById(eventId));
        comment.setCreatedAt(LocalDateTime.now());
        Long commentId = commentRepository.save(comment).getId();

        // Attempt to delete the comment as the original user
        ConditionNotMetException exception = assertThrows(ConditionNotMetException.class,
                () -> commentService.delete(commentId, userId));
        assertThat(exception.getMessage()).isEqualTo("You can only delete your own comments");
    }

    @Test
    public void testGetCommentsByPostId() {
        // Create a few comments for the event
        for (int i = 1; i <= 3; i++) {
            Comment comment = new Comment();
            comment.setText("Comment " + i);
            comment.setUser(userService.getEntityById(userId));
            comment.setEvent(eventService.getEntityById(eventId));
            comment.setCreatedAt(LocalDateTime.now().minusMinutes(i));
            commentRepository.save(comment);
        }

        // Get the comments
        List<CommentDto> comments = commentService.getCommentsByPostId(eventId, 0, 10, "DESC");

        assertThat(comments).isNotNull();
        assertThat(comments).hasSize(3);
        assertThat(comments.get(0).getText()).isEqualTo("Comment 1");
    }
}