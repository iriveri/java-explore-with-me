package ru.practicum.comment.controller;

import jakarta.validation.constraints.Min;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;
import ru.practicum.dto.comment.CommentDto;

import java.util.List;

@RestController
@Slf4j
@RequestMapping("/comments")
public class PublicCommentController {

    private final CommentService commentService;

    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Получить пагинированные комментарии с возможностью сортировки.
     *
     * @param eventId       идентификатор поста, для которого нужно получить комментарии
     * @param from          количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size          количество элементов в наборе
     * @param sortDirection направление сортировки: ASC для старых комментариев или DESC для новых
     * @return {@link ResponseEntity} содержащий список комментариев и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(
            @RequestParam Long eventId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "DESC") String sortDirection) {
        log.debug("Endpoint GET /comments has been reached with eventId: {} from: {} size: {} sortDirection: {}", eventId, from, size, sortDirection);

        List<CommentDto> comments = commentService.getCommentsByPostId(eventId, from, size, sortDirection);
        log.info("Comment's list fetched successfully with {} comments", comments.size());
        return ResponseEntity.ok(comments);
    }

    /**
     * Получение комментария по его id.
     *
     * @return {@link ResponseEntity} содержащий комментарий и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping("/{commentId}")
    public ResponseEntity<CommentDto> getComment(
            @PathVariable Long commentId) {
        log.debug("Endpoint GET /comments/{} has been reached", commentId);

        CommentDto comment = commentService.getById(commentId);
        log.info("Comment {} fetched successfully", commentId);
        return ResponseEntity.ok(comment);
    }
}