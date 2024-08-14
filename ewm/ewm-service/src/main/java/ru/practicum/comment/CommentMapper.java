package ru.practicum.comment;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingConstants;
import org.mapstruct.ReportingPolicy;
import ru.practicum.dto.comment.CommentDto;
import ru.practicum.dto.comment.NewCommentDto;
import ru.practicum.dto.comment.ShortCommentDto;


@Mapper(componentModel = MappingConstants.ComponentModel.SPRING,
        unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface CommentMapper {
    Comment fromDto(NewCommentDto newComment);

    @Mapping(source = "event.id", target = "eventId")
    ShortCommentDto toShortDto(Comment comment);

    @Mapping(source = "event.id", target = "eventId")
    CommentDto toDto(Comment comment);
}
