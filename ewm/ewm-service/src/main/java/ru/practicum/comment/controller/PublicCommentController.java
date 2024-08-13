package ru.practicum.comment.controller;

import jakarta.validation.constraints.Min;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import ru.practicum.comment.service.CommentService;
import ru.practicum.dto.comment.CommentDto;

import java.util.List;

@RestController
@RequestMapping("/comments")
public class PublicCommentController {

    private final CommentService commentService;

    @Autowired
    public PublicCommentController(CommentService commentService) {
        this.commentService = commentService;
    }

    /**
     * Получить пагинированные комментарии с возможностью сортировки.
     *
     * @param postId        идентификатор поста, для которого нужно получить комментарии
     * @param from          количество элементов, которые нужно пропустить для формирования текущего набора
     * @param size          количество элементов в наборе
     * @param sortDirection направление сортировки: ASC для старых комментариев или DESC для новых
     * @return {@link ResponseEntity} содержащий список комментариев и статус ответа {@link HttpStatus#OK}
     */
    @GetMapping
    public ResponseEntity<List<CommentDto>> getComments(
            @RequestParam Long postId,
            @RequestParam(value = "from", defaultValue = "0") @Min(0) int from,
            @RequestParam(value = "size", defaultValue = "10") @Min(1) int size,
            @RequestParam(defaultValue = "DESC") String sortDirection) {

        List<CommentDto> comments = commentService.getCommentsByPostId(postId, from, size, sortDirection);
        return ResponseEntity.ok(comments);
    }
}