package ru.practicum.controller;

import lombok.extern.slf4j.Slf4j;
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
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.service.CommentService;

import javax.validation.Valid;

@RestController
@Slf4j
@Validated
@RequestMapping("/{userId}/comments")
public class PrivateCommentController {

    private final CommentService commentService;

    public PrivateCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Добавление нового комментария.
     *
     * @param commentDto {@link NewCommentDto} данные добавляемого комментария
     * @return {@link ResponseEntity} содержащий объект {@link CommentDto} и статус ответа {@link HttpStatus#CREATED}
     */
    @PostMapping
    public ResponseEntity<CommentDto> addComment(@Valid @RequestBody NewCommentDto commentDto, @PathVariable Long userId) {
        log.debug("Endpoint POST /comments has been reached with CommentDto: {}", commentDto);
        CommentDto createdComment = commentService.addComment(commentDto,userId);
        log.info("Comment {} created successfully", createdComment.getId());
        return new ResponseEntity<>(createdComment, HttpStatus.CREATED);
    }

    /**
     * Редактирование своего комментария.
     *
     * @param commentId идентификатор комментария
     * @param newText   новый текст комментария
     * @return {@link ResponseEntity} содержащий обновлённый объект {@link CommentDto} и статус ответа {@link HttpStatus#OK}
     */
    @PutMapping("/{commentId}")
    public ResponseEntity<CommentDto> editComment(
            @PathVariable Long commentId,
            @PathVariable Long userId,
            @RequestBody String newText) {

        log.debug("Endpoint PUT /comments/{} has been reached with newText: {}", commentId, newText);

        CommentDto updatedComment = commentService.editComment(commentId,userId, newText);
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
    public ResponseEntity<Void> deleteComment(@PathVariable Long commentId,            @PathVariable Long userId) {

        log.debug("Endpoint DELETE /comments/{} has been reached", commentId);
        commentService.deleteComment(commentId,userId);
        log.info("Comment {} deleted successfully", commentId);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}