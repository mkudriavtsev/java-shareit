package ru.practicum.shareit.item.mapper;

import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(uses = {BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    ItemDto toItemDto(Item item);

    List<ItemDto> toItemDtoList(List<Item> items);

    Item toEntityFromCreateItemDto(CreateItemDto dto);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItemFromDto(PatchItemDto itemDto, @MappingTarget Item item);
}
