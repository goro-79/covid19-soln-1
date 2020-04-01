package live.goro.covid19.handler;

import live.goro.covid19.service.DataService;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import static live.goro.covid19.handler.DataHandler.CITIES;
import static org.assertj.core.api.Assertions.assertThat;
import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;

@ExtendWith(MockitoExtension.class)
@Slf4j
public class DataHandlerTest {
    @Mock
    private DataService service;

    private WebTestClient client;

    @BeforeEach
    public void setUp() {
        DataHandler handler = DataHandler.instance(service);
        client = bindToRouterFunction(handler.router())
                .build();
    }

    @Test
    public void getCities_test01() {
        // given

        //BDDMockito.given(service.createRequests(BDDMockito.any())).willReturn(Flux.empty());

        // when
        client.get()
                .uri(CITIES)
                .accept(TEXT_EVENT_STREAM)
                .exchange()
                .expectStatus().isOk();

    }

}
