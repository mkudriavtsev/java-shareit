package ru.practicum.shareit.request.mapper;

import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import ru.practicum.shareit.item.mapper.ItemMapper;
import ru.practicum.shareit.request.dto.CreateItemRequestDto;
import ru.practicum.shareit.request.dto.ItemRequestDto;
import ru.practicum.shareit.request.model.ItemRequest;

import java.util.List;

@Mapper(uses = ItemMapper.class)
public interface ItemRequestMapper {

    @Mapping(target = "id", ignore = true)
    @Mapping(target = "requester", ignore = true)
    @Mapping(target = "created", ignore = true)
    ItemRequest toEntity(CreateItemRequestDto dto);

    @Mapping(target = "items", ignore = true)
    ItemRequestDto toDto(ItemRequest itemRequest);

    List<ItemRequestDto> toDtoList(List<ItemRequest> itemRequests);
}
