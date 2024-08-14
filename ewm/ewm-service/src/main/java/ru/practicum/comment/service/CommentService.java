package ru.practicum.comment.service;

import ru.practicum.comment.Comment;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;

import java.util.List;

public interface CommentService {
    ShortCommentDto create(NewCommentDto commentDto, Long userId);

    ShortCommentDto edit(Long commentId, Long userId, String newText);

    void delete(Long commentId, Long userId);

    void delete(Long commentId);

    void pinComment(Long commentId);

    void banUserFromCommenting(Long userId, Long postId);

    List<CommentDto> getCommentsByPostId(Long postId, int from, int size, String sortDirection);

    CommentDto getById(Long commentId);

    Comment getEntityById(Long commentId);
}
