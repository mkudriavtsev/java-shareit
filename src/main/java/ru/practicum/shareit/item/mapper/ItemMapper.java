package ru.practicum.shareit.item.mapper;

import org.mapstruct.*;
import ru.practicum.shareit.booking.mapper.BookingMapper;
import ru.practicum.shareit.item.dto.CreateItemDto;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.dto.ItemInRequestDto;
import ru.practicum.shareit.item.dto.PatchItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper(uses = {BookingMapper.class, CommentMapper.class})
public interface ItemMapper {

    @Mapping(target = "lastBooking", ignore = true)
    @Mapping(target = "nextBooking", ignore = true)
    @Mapping(target = "comments", ignore = true)
    @Mapping(target = "requestId", source = "item.request.id")
    ItemDto toItemDto(Item item);

    List<ItemDto> toItemDtoList(List<Item> items);

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    Item toEntity(CreateItemDto dto);

    @Mapping(target = "owner", ignore = true)
    @Mapping(target = "request", ignore = true)
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    void updateItem(PatchItemDto itemDto, @MappingTarget Item item);

    @Mapping(target = "requestId", source = "item.request.id")
    ItemInRequestDto toItemInRequestDto(Item item);

    List<ItemInRequestDto> toItemInRequestDtoList(List<Item> items);
}
