package live.goro.covid19.service;

import live.goro.covid19.domain.User;
import live.goro.covid19.dto.UserDTO;
import live.goro.covid19.repository.UserRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static live.goro.common.web.functions.object.ObjectFunctions.createCopy;
import static live.goro.covid19.utils.TestUtil.buildUser;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static reactor.core.publisher.Mono.just;

@ExtendWith(MockitoExtension.class)
public class UserServiceTest {
    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private UserService userService;

    @Test
    public void shouldFindUserByEmail() {
        // given
        User user = buildUser(5);
        given(userRepository.findByEmailIgnoreCase(user.getEmail())).willReturn(just(user));

        //when
        Mono<UserDTO> userMono = userService.getUserByEmail(user.getEmail());

        // then
        StepVerifier.create(userMono)
                .assertNext(userDTO -> {
                    UserDTO inputUserDTO = createCopy(user, UserDTO.class).block();
                    assertThat(userDTO).isEqualToIgnoringGivenFields(inputUserDTO);
                })
                .verifyComplete();
    }

   /* //@Test todo may be can be removed
    public void shouldUpdateUserByEmail() {
        // given
        UserDTO userDTO = buildUserDTO(5);
        User transformedUsers = createCopy(userDTO, User.class)
                .block();
        ArgumentCaptor<User> userCaptor = ArgumentCaptor.forClass(User.class);
        given(userRepository.save(userCaptor.capture())).willReturn(just(transformedUsers));

        //when
        Mono<UserDTO> userMono = userService.updateUser(userDTO);

        // then
        StepVerifier.create(userMono)
                .consumeNextWith(value -> {
                    assertThat(value).isEqualToIgnoringGivenFields(userDTO, "requestedItems", "offeredItems");
                    assertItemsStatus(value);
                })
                .verifyComplete();
    }
*/
   /* private void assertItemsStatus(UserDTO updatedUserDTO) {
        updatedUserDTO.getRequestedItems()
                .forEach(item -> {
                    assertThat(item.getStatus()).isEqualTo(OPEN);
                });
    }*/
}
