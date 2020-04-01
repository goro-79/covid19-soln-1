package live.goro.covid19.handler;

import live.goro.common.web.functions.handler.HandlerHelper;
import live.goro.common.web.functions.handler.Responder;
import live.goro.covid19.domain.AbstractItem;
import live.goro.covid19.domain.AbstractItem.ItemType;
import live.goro.covid19.dto.ItemDTO;
import live.goro.covid19.service.ItemService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;

import static java.util.Map.of;
import static java.util.stream.Collectors.joining;
import static org.springframework.http.MediaType.*;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;

@Service
@RequiredArgsConstructor
@Slf4j
public class ItemHandler {
    public static final String ITEMS = "/items";
    public static final String ITEMS_PATH = "/items/{type}";
    private final ItemService itemService;

    public static ItemHandler instance(ItemService itemService) {
        return new ItemHandler(itemService);
    }

    public RouterFunction<ServerResponse> router() {
        return route()
                .GET(ITEMS, accept(TEXT_EVENT_STREAM), this::getItems)
                .POST(ITEMS_PATH, contentType(APPLICATION_JSON), this::addNewItems)
                .build();
    }

    private Mono<ServerResponse> getItems(ServerRequest request) {
        MultiValueMap<String, String> queryParams = request.queryParams();
        String userId = queryParams.getFirst("userId");
        String type = queryParams.getFirst("type");

        // todo check if logged in user is only getting items for himself or someone else
        Flux<ItemDTO> items = Flux.empty();
        if (Objects.equals(type, "offered")) {
            if (userId != null) {
                items = itemService.getOfferedItems(userId);
            } else {
                String neUserId = queryParams.getFirst("ne-userId");
                String name = queryParams.getFirst("name");
                if (neUserId != null && name != null) {
                    items = itemService.getOfferedItems(neUserId, name);
                } else {
                    items = Flux.empty();
                }
            }
        } else {
            items = itemService.getRequestedItems(userId);
        }
        Flux<ServerSentEvent<ItemDTO>> serverSentEvents = items.map(item -> HandlerHelper.toSSE(item.getId(), item))
                .concatWithValues(HandlerHelper.completionEvent(ItemDTO.builder().build()));
        return Responder.respondOk(serverSentEvents, ServerSentEvent.class, TEXT_EVENT_STREAM);
    }


    private Mono<ServerResponse> addNewItems(ServerRequest request) {
        // todo verify only current logged in user is calling it
        String type = request.pathVariable("type");
        Flux<ItemDTO> newItems = request.bodyToFlux(ItemDTO.class);
        Flux<? extends AbstractItem> savedItems = itemService.createItems(newItems, ItemType.from(type));
        //.doOnNext(item -> log.info("New user item saved : {}", item));
        return Responder.respondOk(savedItems, AbstractItem.class, APPLICATION_JSON);
    }

}
