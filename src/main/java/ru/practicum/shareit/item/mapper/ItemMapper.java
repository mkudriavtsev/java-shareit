package ru.practicum.shareit.item.mapper;

import org.mapstruct.Mapper;
import ru.practicum.shareit.item.dto.ItemDto;
import ru.practicum.shareit.item.model.Item;

import java.util.List;

@Mapper
public interface ItemMapper {
    Item toEntity(ItemDto dto);

    ItemDto toDto(Item item);

    List<Item> toItemList(List<ItemDto> itemDtos);

    List<ItemDto> toItemDtoList(List<Item> items);
}
