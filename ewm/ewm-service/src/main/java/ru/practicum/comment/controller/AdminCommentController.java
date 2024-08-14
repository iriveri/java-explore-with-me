package ru.practicum.comment.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;

@RestController
@Slf4j
@Validated
@RequestMapping("/admin/comments")
public class AdminCommentController {

    private final CommentService commentService;

    @Autowired
    public AdminCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Удаление комментария модератором.
     * не предпологает какой либо валидации
     *
     * @param commentId идентификатор комментария
     * @return {@link ResponseEntity} содержащий статус ответа {@link HttpStatus#NO_CONTENT} или {@link HttpStatus#NOT_FOUND} в случае ошибки
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId) {
        log.debug("Endpoint DELETE /admin/comments/{} has been reached", commentId);

        commentService.delete(commentId);
        log.info("Comment {} deleted successfully by moderator", commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }


    /**
     * Запретить пользователю оставлять комментарии под данным событием.
     * ЗАпрещает оставлять коментарии под конкретным событием
     *
     * @param userId  идентификатор пользователя
     * @param eventId идентификатор события
     * @return {@link ResponseEntity} содержащий статус ответа {@link HttpStatus#OK} или {@link HttpStatus#NOT_FOUND} в случае ошибки
     */
    @PostMapping("/ban/{userId}/post/{eventId}")
    public ResponseEntity<Void> banUserFromCommenting(@PathVariable Long userId, @PathVariable Long eventId) {
        log.debug("Endpoint POST /admin/comments/ban/{}/post/{} has been reached", userId, eventId);

        commentService.banUserFromCommenting(userId, eventId);
        log.info("User {} has been banned from commenting on post {}", userId, eventId);
        return new ResponseEntity<>(HttpStatus.OK);
    }

    /**
     * Закрепить комментарий.
     * Закрепляет сообщение таким образом при получения списка коментариев к событию
     * Данные сообщения будут первыми
     *
     * @param commentId идентификатор комментария
     * @return {@link ResponseEntity} содержащий статус ответа {@link HttpStatus#OK} или {@link HttpStatus#NOT_FOUND} в случае ошибки
     */
    @PostMapping("/pin/{commentId}")
    public ResponseEntity<Void> pinComment(@PathVariable Long commentId) {
        log.debug("Endpoint POST /admin/comments/pin/{} has been reached", commentId);

        commentService.pinComment(commentId);
        log.info("Comment {} has been pinned successfully", commentId);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
