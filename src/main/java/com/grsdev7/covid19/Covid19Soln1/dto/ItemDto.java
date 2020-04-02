package com.grsdev7.covid19.Covid19Soln1.dto;

import com.grsdev7.covid19.Covid19Soln1.domain.Item;
import lombok.Builder;
import lombok.Data;
import lombok.Value;
import lombok.With;

import java.time.Instant;
import java.util.Set;

import static java.util.stream.Collectors.toSet;

@Data
@Builder
@With
public class ItemDto {
    private String name;
    private int quantity;
    private Instant dueDate;


    public static Set<Item> convertToItems(Set<ItemDto> itemDtos) {
        return itemDtos.stream()
                .map(ItemDto::convertToItem)
                .collect(toSet());
    }

    public static Item convertToItem(ItemDto itemDto) {
        return Item.builder()
                .name(itemDto.getName())
                .quantity(itemDto.getQuantity())
                .dueDate(itemDto.getDueDate())
                .build();
    }

    public static Set<ItemDto> convertToItemDtos(Set<Item> items) {
        return items.stream()
                .map(ItemDto::convertToItemDto)
                .collect(toSet());
    }

    private static ItemDto convertToItemDto(Item item) {
        return ItemDto.builder()
                .name(item.getName())
                .quantity(item.getQuantity())
                .dueDate(item.getDueDate())
                .build();
    }


}
