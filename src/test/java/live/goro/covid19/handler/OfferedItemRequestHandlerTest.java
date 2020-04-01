package live.goro.covid19.handler;

import live.goro.common.web.functions.object.ObjectFunctions;
import live.goro.covid19.domain.AbstractItem;
import live.goro.covid19.domain.OfferedItem;
import live.goro.covid19.domain.OfferedItemRequest;
import live.goro.covid19.dto.ItemDTO;
import live.goro.covid19.service.OfferedItemRequestService;
import live.goro.covid19.domain.Status;
import grsdev7.functions.util.CommonFunctions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuples;

import java.time.Instant;

import static live.goro.covid19.handler.OfferedItemRequestHandler.PATH_ITEM;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class OfferedItemRequestHandlerTest {
    @Mock
    private OfferedItemRequestService service;

    private WebTestClient client;

    @BeforeEach
    public void setUp() {
        OfferedItemRequestHandler handler = OfferedItemRequestHandler.instance(service);
        client = bindToRouterFunction(handler.router())
                .build();
    }

    @Test
    public void addNewItems_test1_offeredItems() {
        // given
        OfferedItemRequest newItem = OfferedItemRequest.builder()
                .offeredItemId(CommonFunctions.randomUUID())
                .userId(CommonFunctions.randomUUID())
                .build();
        Flux<OfferedItemRequest> newItems = Flux.just(newItem);

        BDDMockito.given(service.createRequests(BDDMockito.any())).willReturn(Flux.empty());

        // when
        FluxExchangeResult<OfferedItemRequest> items = client.post()
                .uri(OfferedItemRequestHandler.PATH)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(newItems, OfferedItemRequest.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(OfferedItemRequest.class);

    }

    @Test
    void patchItemRequest() {
        // given
        String requestId = CommonFunctions.randomUUID();
        BDDMockito.given(service.patchRequest(BDDMockito.any(), BDDMockito.any()))
        .willReturn(Mono.empty());

        // when
        client.patch()
                .uri(PATH_ITEM, requestId)
                .contentType(APPLICATION_JSON)
                .accept(APPLICATION_JSON)
                .bodyValue("{\"itemOwnerConsent\":\"true\"}")
                .exchange()
                .expectStatus().isNoContent();
    }


    private AbstractItem toSavedItem(ItemDTO newItem) {
        return ObjectFunctions.copy(newItem, OfferedItem.class,
                Tuples.of("id", CommonFunctions::randomUUID),
                Tuples.of("createdOn", Instant::now),
                Tuples.of("status", () -> Status.OPEN));
    }

    private void assertItem(OfferedItem offeredItem) {
        assertThat(offeredItem.getId()).isNotNull();
        assertThat(offeredItem.getName()).isNotNull();
        assertThat(offeredItem.getQuantity()).isNotNull();
    }
}
