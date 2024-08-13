package ru.practicum.comment.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import ru.practicum.comment.*;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.event.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.user.User;
import ru.practicum.user.service.UserService;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class CommentServiceImpl implements CommentService {

    private final CommentRepository commentRepository;
    private final UserService userService;
    private final EventService eventService;
    private final CommentMapper commentMapper;
    private final BanRepository banRepository;

    @Autowired
    public CommentServiceImpl(CommentRepository commentRepository, UserService userService, EventService eventService, CommentMapper commentMapper, BanRepository banRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.eventService = eventService;
        this.commentMapper = commentMapper;
        this.banRepository = banRepository;
    }

    public CommentDto addComment(NewCommentDto commentDto, Long userId) {
        // Проверяем, запрещено ли пользователю комментировать это событие
        boolean isBanned = banRepository.findByEventIdAndUserId(commentDto.getEventId(), userId).isPresent();
        if (isBanned) {
            throw new IllegalArgumentException("User is banned from commenting on this event");
        }

        // Создаем новый комментарий
        Comment comment = commentMapper.fromDto(commentDto);
        comment.setUser(userService.getEntityById(userId)); // Получаем текущего пользователя
        comment.setEvent(eventService.getEntityById(commentDto.getEventId()));
        comment.setCreatedAt(LocalDateTime.now());

        // Сохраняем комментарий в репозиторий и возвращаем DTO
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toDto(savedComment);
    }

    public CommentDto editComment(Long commentId, Long userId, String newText) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        if (!comment.getUser().equals(userService.getEntityById(userId))) {
            throw new IllegalArgumentException("You can only edit your own comments");
        }

        if (Duration.between(comment.getCreatedAt(), LocalDateTime.now()).toMinutes() > 15) {
            throw new IllegalArgumentException("Comment edit time expired");
        }

        comment.setText(newText);
        comment.setEditedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toDto(updatedComment);
    }

    public void deleteComment(Long commentId, Long userId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));
        //проверка на владельца поста
        if (!comment.getUser().equals(userService.getEntityById(userId))) {
            throw new IllegalArgumentException("You can only delete your own comments");
        }


        commentRepository.delete(comment);
    }

    public void deleteCommentAsModerator(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        commentRepository.delete(comment);
    }

    @Override
    public void pinComment(Long commentId) {
        Comment comment = commentRepository.findById(commentId)
                .orElseThrow(() -> new IllegalArgumentException("Comment not found"));

        comment.setPinned(true);
        commentRepository.save(comment);
    }

    @Override
    public void banUserFromCommenting(Long userId, Long eventId) {
        User user = userService.getEntityById(userId);
        Event event = eventService.getEntityById(eventId);

        CommentBan ban = new CommentBan();
        ban.setUser(user);
        ban.setEvent(event);
        banRepository.save(ban);
    }

    @Override
    public List<CommentDto> getCommentsByPostId(Long eventId, int from, int size, String sortDirection) {

        Sort.Direction direction;
        try {
            direction = Sort.Direction.fromString(sortDirection);
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid sort direction: " + sortDirection);
        }

        Pageable pageable = PageRequest.of(from, size, Sort.by(direction, "createdAt"));
        //сделать так чтобы первыми возвращались закреплённые
        Page<Comment> commentsPage = commentRepository.findByEventId(eventId, pageable);
        return commentsPage.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

}