package live.goro.covid19.handler;


import live.goro.covid19.dto.UserDTO;
import live.goro.covid19.dto.UserCreationDTO;
import live.goro.covid19.service.UserService;
import live.goro.covid19.utils.TestUtil;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.web.reactive.server.WebTestClient;

import java.time.Duration;

import static live.goro.common.web.functions.object.ObjectFunctions.createCopy;
import static live.goro.common.web.functions.object.ObjectFunctions.toJson;
import static live.goro.covid19.utils.TestUtil.buildUserCreationDTO;
import static java.lang.Boolean.FALSE;
import static java.lang.Boolean.TRUE;
import static org.mockito.BDDMockito.given;
import static org.springframework.http.MediaType.APPLICATION_JSON;
import static org.springframework.test.web.reactive.server.WebTestClient.bindToRouterFunction;
import static reactor.core.publisher.Mono.just;


@ExtendWith(MockitoExtension.class)
@Slf4j
public class UserHandlerTest {
    @Mock
    private UserService userService;

    private WebTestClient client;

    @BeforeEach
    public void setUp() {
        UserHandler handler = UserHandler.instance(userService);
        client = bindToRouterFunction(handler.router())
                .build();
    }


    @Test
    public void shouldCheckIfUserWithEmailExist() {
        // given
        String email = "abc@mail.com";
        given(userService.existsByEmail(email)).willReturn(just(TRUE));

        //then
        client.head()
                .uri("/users?email=" + email)
                .exchange()
                .expectStatus()
                .isOk();
    }

    @Test
    public void shouldCheckIfUserWithEmailExist1() {
        // given
        String email = "abc@mail.com";
        given(userService.existsByEmail(email)).willReturn(just(FALSE));

        //then
        client.head()
                .uri("/users?email=" + email)
                .exchange()
                .expectStatus()
                .isNotFound();
    }

    @Test
    public void shouldGetUserByEmail() {
        // given
        UserDTO userDTO = TestUtil.buildNewUserDTO();
        given(userService.getUserByEmail(userDTO.getEmail())).willReturn(just(userDTO));

        //then
        client.mutate().responseTimeout(Duration.ofMinutes(100)).build()
                .get()
                .uri("/users?email=" + userDTO.getEmail())
                .exchange()
                .expectStatus()
                .isOk()
                .expectBody().consumeWith(response -> System.out.println("[Response] : " + response))
        ;
    }

/*    @Test may be not working because response is publisher Mono
    public void shouldHandleNotFound() {
        // given
        String email = "abc@email.com";
        given(userService.getUserByEmail(email)).willReturn(Mono.empty());

        //then
        client.get()
                .uri("/users?email="+email)
                .exchange()
                .expectStatus()
                .isNotFound();
    }*/

    @Test
    public void shouldCreateNewUser() {
        //given
        UserCreationDTO userCreationDTO = buildUserCreationDTO();
        UserDTO userDTO = toUserDTO(userCreationDTO);
        given(userService.createUser(userCreationDTO)).willReturn(just(userDTO));

        // when n then
        client.post()
                .uri("/users/")
                .contentType(APPLICATION_JSON)
                .bodyValue(toJson(userCreationDTO))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectHeader()
                .contentType(APPLICATION_JSON)
                .expectStatus().isCreated()
                .expectBody()
                .json(toJson(userDTO))
        ;
    }

    @Test
    public void shouldValidateAndBlockCreation() {
        //given
        UserCreationDTO userCreationDTO = UserCreationDTO.builder().build();

        // when n then
        client
                .mutate().responseTimeout(Duration.ofMinutes(3)).build()
                .post()
                .uri("/users/")
                .contentType(APPLICATION_JSON)
                .bodyValue(toJson(userCreationDTO))
                .accept(APPLICATION_JSON)
                .exchange()
                .expectStatus().isBadRequest()
                .expectBody()
                .consumeWith(System.out::println)
        ;
    }


    // todo add test case for getItems when email of logged in user is missing


    private UserDTO toUserDTO(UserCreationDTO userCreationDTO) {
        return createCopy(userCreationDTO, UserDTO.class).block();
    }

}
