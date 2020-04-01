package live.goro.covid19.handler;

import live.goro.common.web.functions.object.ObjectFunctions;
import live.goro.covid19.domain.AbstractItem;
import live.goro.covid19.domain.OfferedItem;
import live.goro.covid19.dto.ItemDTO;
import live.goro.covid19.service.ItemService;
import live.goro.covid19.domain.Status;
import grsdev7.functions.util.CommonFunctions;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.BDDMockito;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.FluxExchangeResult;
import org.springframework.test.web.reactive.server.WebTestClient;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuples;

import java.time.Instant;

import static live.goro.covid19.utils.TestUtil.buildNewItemDTO;
import static live.goro.covid19.utils.TestUtil.buildOfferedItems;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class ItemHandlerTest {
    @Mock
    private ItemService itemService;

    private WebTestClient client;

    @BeforeEach
    public void setUp() {
        ItemHandler handler = ItemHandler.instance(itemService);
        client = bindToRouterFunction(handler.router())
                .build();
    }

    @Test
    public void getItems_test1_SSE() {
        // given
        String userId = CommonFunctions.randomUUID();
        Flux<OfferedItem> offeredItems = buildOfferedItems();
        given(itemService.getOfferedItems(userId)).willReturn(offeredItems.map(ItemDTO::from));

        //then
        client.get()
                .uri("/items?userId=" + userId + "&type=offered")
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody().consumeWith(response -> System.out.println("[Response] : " + response)) //todo add more verification
        ;
    }

    @Test
    public void addItems_test1_offeredItems() {
        // given
        ItemDTO newItem = buildNewItemDTO();
        Flux<ItemDTO> newItems = Flux.just(newItem, newItem);
        Flux<? extends AbstractItem> savedItems = newItems.map(this::toSavedItem);
        ArgumentCaptor<Flux<ItemDTO>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);
        String type = "offered";
        BDDMockito.given(itemService.createItems(BDDMockito.any(), BDDMockito.any()))
                .willReturn(Flux.empty()); // todo check commented part, they should work


        //then   POST items/offered {userId : 'abc123', items : [{},{}]}
        FluxExchangeResult<OfferedItem> items = client.post()
                .uri("/items/" + type)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(newItems, ItemDTO.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(OfferedItem.class);

/*        StepVerifier.create(items.getResponseBody().log())
                .expectNext(savedItems.blockFirst())
                .expectNext(savedItems.blockLast())
                .verifyComplete();
        Flux<NewItemDTO> captured = argumentCaptor.getValue();
        StepVerifier.create(captured)
                .expectNext(newItem)
                .expectNext(newItem)
                .verifyComplete();*/

    }

    @Test
    public void addItems_test2_requestedItems() {
        // given
        String userId = CommonFunctions.randomUUID();
        ItemDTO newItem = buildNewItemDTO();
        Flux<ItemDTO> newItems = Flux.just(newItem, newItem);
        Flux<? extends AbstractItem> savedItems = newItems.map(this::toSavedItem);
        ArgumentCaptor<Flux<ItemDTO>> argumentCaptor = ArgumentCaptor.forClass(Flux.class);
        String type = "requested";
        BDDMockito.given(itemService.createItems(BDDMockito.any(), BDDMockito.any()))
                .willReturn(Flux.empty()); // todo check commented part, they should work


        //then   POST items/offered {userId : 'abc123', items : [{},{}]}
        FluxExchangeResult<AbstractItem> items = client.post()
                .uri("/items/" + type)
                .accept(APPLICATION_JSON)
                .contentType(APPLICATION_JSON)
                .body(newItems, ItemDTO.class)
                .exchange()
                .expectStatus().isOk()
                .returnResult(AbstractItem.class);

/*        StepVerifier.create(items.getResponseBody().log())
                .expectNext(savedItems.blockFirst())
                .expectNext(savedItems.blockLast())
                .verifyComplete();
        Flux<NewItemDTO> captured = argumentCaptor.getValue();
        StepVerifier.create(captured)
                .expectNext(newItem)
                .expectNext(newItem)
                .verifyComplete();*/

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
