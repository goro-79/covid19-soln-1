package live.goro.covid19.handler;

import live.goro.common.web.functions.handler.Responder;
import live.goro.covid19.domain.OfferedItemRequest;
import live.goro.covid19.service.OfferedItemRequestService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Map;

import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.contentType;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Service
@RequiredArgsConstructor
@Slf4j
public class OfferedItemRequestHandler {
    public static final String PATH = "/users/offered_items/requests";
    public static final String REQUEST_ID = "request_id";
    public static final String PATH_ITEM = "/users/offered_items/requests/" + "{" + REQUEST_ID + "}";
    private final OfferedItemRequestService service;

    public static OfferedItemRequestHandler instance(OfferedItemRequestService service) {
        return new OfferedItemRequestHandler(service);
    }

    public RouterFunction<ServerResponse> router() {
        return route()
                .POST(PATH, contentType(APPLICATION_JSON), this::addNewItems)
                .PATCH(PATH_ITEM, contentType(APPLICATION_JSON), this::patchRequest)
                .build();
    }

    Mono<ServerResponse> addNewItems(ServerRequest request) {
        Flux<OfferedItemRequest> newItems = request.bodyToFlux(OfferedItemRequest.class); // todo verify only current logged in user is calling it
        Flux<OfferedItemRequest> savedItems = service.createRequests(newItems);
        return Responder.respondOk(savedItems, OfferedItemRequest.class, APPLICATION_JSON);
    }

    private Mono<ServerResponse> patchRequest(ServerRequest serverRequest) {
        String requestId = serverRequest.pathVariable(REQUEST_ID);
        Mono<Map> request = serverRequest.bodyToMono(Map.class);
        service.patchRequest(requestId, request).subscribe();
        return Responder.noContent();
    }

}
