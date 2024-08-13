package ru.practicum.comment.service;

import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;

import java.util.List;

public interface CommentService {
    CommentDto addComment(NewCommentDto commentDto, Long userId);

    CommentDto editComment(Long commentId, Long userId, String newText);

    void deleteComment(Long commentId, Long userId);

    void deleteCommentAsModerator(Long commentId);

    void pinComment(Long commentId);

    void banUserFromCommenting(Long userId, Long postId);

    List<CommentDto> getCommentsByPostId(Long postId, int from, int size, String sortDirection);
}
