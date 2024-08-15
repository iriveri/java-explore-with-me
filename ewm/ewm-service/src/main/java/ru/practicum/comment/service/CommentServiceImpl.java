package ru.practicum.comment.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.comment.BanRepository;
import ru.practicum.comment.Comment;
import ru.practicum.comment.CommentBan;
import ru.practicum.comment.CommentMapper;
import ru.practicum.comment.CommentRepository;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;
import ru.practicum.dto.event.EventState;
import ru.practicum.event.Event;
import ru.practicum.event.service.EventService;
import ru.practicum.exception.ConditionNotMetException;
import ru.practicum.exception.NotFoundException;
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

    public CommentServiceImpl(CommentRepository commentRepository, UserService userService, EventService eventService, CommentMapper commentMapper, BanRepository banRepository) {
        this.commentRepository = commentRepository;
        this.userService = userService;
        this.eventService = eventService;
        this.commentMapper = commentMapper;
        this.banRepository = banRepository;
    }

    @Override
    @Transactional
    public ShortCommentDto create(NewCommentDto commentDto, Long userId) {
        Event event = eventService.getEntityById(commentDto.getEventId());
        if (event.getState() != EventState.PUBLISHED) {
            throw new ConditionNotMetException("Event not yet published to be commented");
        }

        boolean isBanned = banRepository.findByEventIdAndUserId(commentDto.getEventId(), userId).isPresent();
        if (isBanned) {
            throw new ConditionNotMetException("User is banned from commenting on this event");
        }

        Comment comment = commentMapper.fromDto(commentDto);
        comment.setUser(userService.getEntityById(userId));
        comment.setEvent(event);
        comment.setCreatedAt(LocalDateTime.now());
        comment.setPinned(false);
        Comment savedComment = commentRepository.save(comment);
        return commentMapper.toShortDto(savedComment);
    }

    @Override
    @Transactional
    public ShortCommentDto edit(Long commentId, Long userId, String newText) {
        Comment comment = getEntityById(commentId);

        if (!comment.getUser().equals(userService.getEntityById(userId))) {
            throw new ConditionNotMetException("You can only edit your own comments");
        }

        if (Duration.between(comment.getCreatedAt(), LocalDateTime.now()).toMinutes() > 15) {
            throw new ConditionNotMetException("Comment edit time expired");
        }

        comment.setText(newText);
        comment.setEditedAt(LocalDateTime.now());

        Comment updatedComment = commentRepository.save(comment);
        return commentMapper.toShortDto(updatedComment);
    }

    @Override
    @Transactional
    public void delete(Long commentId, Long userId) {
        Comment comment = getEntityById(commentId);

        if (!comment.getUser().equals(userService.getEntityById(userId))) {
            throw new ConditionNotMetException("You can only delete your own comments");
        }

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void delete(Long commentId) {
        Comment comment = getEntityById(commentId);

        commentRepository.delete(comment);
    }

    @Override
    @Transactional
    public void pinComment(Long commentId) {
        Comment comment = getEntityById(commentId);

        comment.setPinned(true);
        commentRepository.save(comment);
    }

    @Override
    @Transactional
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

        Pageable pageable = PageRequest.of(from, size, Sort.by(Sort.Order.desc("pinned")).descending().and(Sort.by(direction, "createdAt")));
        Page<Comment> commentsPage = commentRepository.findByEventId(eventId, pageable);
        return commentsPage.stream()
                .map(commentMapper::toDto)
                .collect(Collectors.toList());
    }

    @Override
    public CommentDto getById(Long commentId) {
        return commentMapper.toDto(getEntityById(commentId));
    }

    @Override
    public Comment getEntityById(Long commentId) {
        return commentRepository.findById(commentId)
                .orElseThrow(() -> new NotFoundException("Comment not found"));
    }
}
