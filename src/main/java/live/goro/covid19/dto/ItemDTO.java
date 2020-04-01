package live.goro.covid19.dto;

import live.goro.common.web.functions.object.ObjectFunctions;
import live.goro.covid19.domain.AbstractItem;
import live.goro.covid19.domain.OfferedItem;
import live.goro.covid19.domain.RequestedItem;
import live.goro.covid19.domain.Status;
import lombok.*;

import java.time.Instant;

import static live.goro.covid19.domain.Status.OPEN;
import static java.util.Optional.ofNullable;

@Builder
@Value
@With
public class ItemDTO {
    String id;

    String name;

    Integer quantity;

    Instant dueDate;

    Status status;

    Instant createdOn;

    Long matchCount;

    UserDTO userDTO;

    public static ItemDTO from(Object source) {
        return ObjectFunctions.copy(source, ItemDTO.class);
    }

    public static ItemDTO from(AbstractItem item) {
        return ItemDTO.builder()
                .id(item.getId())
                .name(item.getName())
                .quantity(item.getQuantity())
                .status(item.getStatus())
                .createdOn(item.getCreatedOn())
                .dueDate(item.getDueDate())
                .build();

    }

    public static OfferedItem toOfferedItem(@NonNull ItemDTO itemDTO) {
        return
                OfferedItem.builder()
                        .name(itemDTO.getName())
                        .quantity(itemDTO.getQuantity())
                        .userId(getUserId(itemDTO))
                        .dueDate(itemDTO.getDueDate())
                        .status(OPEN)
                        .build();
    }

    private static String getUserId(@NonNull ItemDTO itemDTO) {
        return ofNullable(itemDTO.getUserDTO()).map(UserDTO::getId).orElseThrow();
    }

    public static RequestedItem toRequestedItem(@NonNull ItemDTO itemDTO) {
        return
                RequestedItem.builder()
                        .name(itemDTO.getName())
                        .quantity(itemDTO.getQuantity())
                        .userId(getUserId(itemDTO))
                        .dueDate(itemDTO.getDueDate())
                        .status(OPEN)
                        .build();
    }
}


