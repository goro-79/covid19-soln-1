package live.goro.covid19.handler;

import live.goro.common.web.functions.handler.HandlerHelper;
import live.goro.common.web.functions.handler.Responder;
import live.goro.covid19.dto.LocationDTO;
import live.goro.covid19.service.DataService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.codec.ServerSentEvent;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.net.InetAddress;

import static org.springframework.http.MediaType.TEXT_EVENT_STREAM;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;


@Service
@RequiredArgsConstructor
@Slf4j
public class DataHandler {
    public static final String CITIES = "/data/cities";
    private final DataService service;

    public static DataHandler instance(DataService service) {
        return new DataHandler(service);
    }

    public RouterFunction<ServerResponse> router() {
        return route()
                .GET(CITIES, accept(TEXT_EVENT_STREAM), this::getCities)
                .build();
    }

    private Mono<ServerResponse> getCities(ServerRequest request) {
        Flux<LocationDTO> cities = Flux.empty();

        if (request.remoteAddress().isPresent()) {
            InetAddress address = request.remoteAddress().get().getAddress();
            cities = service.getCities(request.remoteAddress().get());
        }

        Flux<ServerSentEvent<LocationDTO>> sse = cities.map(city -> HandlerHelper.toSSE(city.getCity(), city))
                .concatWithValues(HandlerHelper.completionEvent(LocationDTO.builder().build()));
        return Responder.respondOk(sse, LocationDTO.class, TEXT_EVENT_STREAM);
    }

    public LocationDTO buildCityDTO(String cityName) {
        return LocationDTO.builder().city(cityName).build();
    }


    @Configuration
    public static class Config {

        @Bean
        public RouterFunction<ServerResponse> dataHandlerRouter(DataHandler handler) {
            return handler.router();
        }
    }
}
