package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.CommentDto;
import ru.practicum.shareit.item.model.Comment;
import ru.practicum.shareit.user.mapper.UserMapper;

import java.util.List;
import java.util.Objects;

@Mapper(uses = {ItemMapper.class, UserMapper.class})
public abstract class CommentMapper {

    public CommentDto toDto(Comment comment) {
        if (Objects.isNull(comment)) {
            return null;
        }
        CommentDto dto = new CommentDto();
        dto.setId(comment.getId());
        dto.setText(comment.getText());
        dto.setAuthorName(comment.getAuthor().getName());
        dto.setCreated(comment.getCreated());
        return dto;
    }

    public abstract Comment toEntity(CommentDto commentDto);

    public abstract List<CommentDto> toDtoList(List<Comment> commentList);
}
