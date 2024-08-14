package ru.practicum.comment.controller;

import jakarta.validation.Valid;
import jakarta.validation.constraints.Size;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;

@RestController
@Slf4j
@Validated
@RequestMapping("/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    @Autowired
    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Добавление нового комментария.
     *
     * @param commentDto {@link NewCommentDto} данные добавляемого комментария
     * @return {@link ResponseEntity} содержащий объект {@link ShortCommentDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<ShortCommentDto> addComment(@Valid @RequestBody NewCommentDto commentDto, @PathVariable Long userId) {
        log.debug("Endpoint POST /comments has been reached with CommentDto: {}", commentDto);
        ShortCommentDto createdComment = commentService.create(commentDto, userId);
        log.info("Comment {} created successfully", createdComment.getId());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    /**
     * Редактирование своего комментария.
     * В теле сообщения передаётся новый текст
     * Должна сохраняться дата и сам факт редактирования
     *
     * @param commentId идентификатор комментария
     * @param newText   новый текст комментария
     * @return {@link ResponseEntity} содержащий обновлённый объект {@link ShortCommentDto} и статус ответа {@link HttpStatus#OK}
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<ShortCommentDto> editComment(
            @PathVariable Long commentId,
            @PathVariable Long userId,
            @RequestBody @Size(min = 1, max = 200) String newText) {

        log.debug("Endpoint PUT /comments/{} has been reached with newText: {}", commentId, newText);

        ShortCommentDto updatedComment = commentService.edit(commentId, userId, newText);
        log.info("Comment {} updated successfully", commentId);
        return new ResponseEntity<>(updatedComment, HttpStatus.OK);
    }

    /**
     * Удаление своего комментария.
     *
     * @param commentId идентификатор комментария
     * @return {@link ResponseEntity} содержащий статус ответа {@link HttpStatus#NO_CONTENT}
     */
    @DeleteMapping("/{commentId}")
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId, @PathVariable Long userId) {

        log.debug("Endpoint DELETE /comments/{} has been reached", commentId);
        commentService.delete(commentId, userId);
        log.info("Comment {} deleted successfully", commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}