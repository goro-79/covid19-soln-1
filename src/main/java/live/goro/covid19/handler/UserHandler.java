package live.goro.covid19.handler;

import live.goro.covid19.dto.UserDTO;
import live.goro.covid19.dto.UserCreationDTO;
import live.goro.covid19.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;

import java.util.function.Function;

import static java.util.Map.of;
import static live.goro.common.web.functions.handler.HandlerHelper.getEmailFromSessionOrQuery;
import static live.goro.common.web.functions.handler.Responder.*;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.web.reactive.function.server.RequestPredicates.accept;
import static org.springframework.web.reactive.function.server.RouterFunctions.route;
import static reactor.core.publisher.Mono.justOrEmpty;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserHandler {
    public static final String USERS = "/users";
    private final UserService userService;

    public static UserHandler instance(UserService userService) {
        return new UserHandler(userService);
    }

    public RouterFunction<ServerResponse> router() {
        return route()
                .GET(USERS, accept(APPLICATION_JSON), this::getUser)
                .POST(USERS, accept(APPLICATION_JSON), this::createUser)
                .HEAD(USERS, this::userExists)
                .build();
    }

    private Mono<ServerResponse> createUser(ServerRequest request) {
        return request.bodyToMono(UserCreationDTO.class)
                .flatMap(this::createNewUser)
                ;
    }

    private Mono<ServerResponse> getUser(ServerRequest request) {
        Mono<String> emailMono = getEmailFromSessionOrQuery(request);
        Mono<ServerResponse> response = emailMono.map(userService::getUserByEmail)
                .flatMap(value -> respondOk(value, UserDTO.class, APPLICATION_JSON));
        return response;
    }

    private Mono<ServerResponse> userExists(ServerRequest request) {
        String email = request.queryParam("email").orElseThrow();
        Mono<Boolean> existsMono = userService.existsByEmail(email);
        return existsMono.flatMap(value -> value ?
                respondOk() :
                respondNotFound());
    }

/*    //todo will need in future may be
    private Mono<ServerResponse> updateUser(ServerRequest request) {
        return request.bodyToMono(UserDTO.class)
                .flatMap(userService::updateUser)
                .flatMap(Responder::respondOk)
                .onErrorResume(handleError());
    }*/


    private Function<? super Throwable, ? extends Mono<? extends ServerResponse>> handleError() {
        return th -> respondBadRequest(of("error", th.getMessage()));
    }

    private Mono<ServerResponse> createNewUser(UserCreationDTO userCreationDTO) {
        Mono<UserDTO> createdUser = userService.createUser(userCreationDTO);
        return createdUser.flatMap(value -> respondCreated(value, USERS + "/" + value.getId()));
    }

}
