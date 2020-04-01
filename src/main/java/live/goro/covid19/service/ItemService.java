package live.goro.covid19.service;

import live.goro.covid19.domain.AbstractItem;
import live.goro.covid19.domain.OfferedItem;
import live.goro.covid19.domain.RequestedItem;
import live.goro.covid19.dto.ItemDTO;
import live.goro.covid19.dto.UserDTO;
import live.goro.covid19.repository.OfferedItemRepository;
import live.goro.covid19.repository.RequestedItemRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import static live.goro.covid19.dto.ItemDTO.from;

@Component
@RequiredArgsConstructor
@Slf4j
public class ItemService {
    private final RequestedItemRepository requestedItemRepository;
    private final OfferedItemRepository offeredItemRepository;
    private final UserService userService;

    public Flux<ItemDTO> getOfferedItems(String userId) {
        return offeredItemRepository.findAllByUserId(userId)
                .map(ItemDTO::from);
    }

    public Flux<ItemDTO> getOfferedItems(String neUserId, String name) {
        return
                offeredItemRepository.findAllByUserIdNotAndNameContainingOrderByCreatedOnDesc(neUserId, name)
                        .flatMap(this::toItemDTO);
    }

    private Mono<ItemDTO> toItemDTO(OfferedItem offeredItem) {
        ItemDTO itemDTO = ItemDTO.from(offeredItem);
        return
                userService.getUserById(offeredItem.getUserId())
                        .map(ownerUser -> {
                            UserDTO userDTO = UserDTO.builder()
                                    .id(ownerUser.getId())
                                    .city(ownerUser.getCity())
                                    .build();
                            return itemDTO.withUserDTO(userDTO);
                        });
    }

    public Flux<ItemDTO> getRequestedItems(String userId) {
        return requestedItemRepository.findAllByUserId(userId)
                .flatMap(this::toItemDTO)
                ;
    }

    private Mono<ItemDTO> toItemDTO(RequestedItem item) {
        Flux<ItemDTO> matchingItems = getOfferedItems(item.getUserId(), item.getName());
        ItemDTO itemDTO = from(item);
        return matchingItems.count().map(itemDTO::withMatchCount);
    }

    public Flux<? extends AbstractItem> createItems(Flux<ItemDTO> newItems, AbstractItem.ItemType itemType) {
        if (AbstractItem.ItemType.OFFERED.equals(itemType)) {
            Flux<OfferedItem> offeredItemFlux = newItems.map(ItemDTO::toOfferedItem);
            return offeredItemRepository.saveAll(offeredItemFlux);
        } else if (AbstractItem.ItemType.REQUESTED.equals(itemType)) {
            Flux<RequestedItem> documentFlux = newItems.map(ItemDTO::toRequestedItem);
            return requestedItemRepository.saveAll(documentFlux);
        }
        throw new IllegalArgumentException("Unsupported item itemType : " + itemType);
    }


    public Mono<OfferedItem> getOfferedItemById(String offeredItemId) {
        return offeredItemRepository.findById(offeredItemId);
    }
}
