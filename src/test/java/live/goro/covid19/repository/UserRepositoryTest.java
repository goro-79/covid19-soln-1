package live.goro.covid19.repository;

import live.goro.covid19.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static live.goro.covid19.utils.TestUtil.buildUser;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
@Slf4j
public class UserRepositoryTest {
    @Autowired
    private UserRepository userRepository;

    @Test
    public void shouldSaveUser() {
        // given
        User user = buildUser(5);
        clearDB();

        // when
        userRepository.save(user).block();

        //then
        Mono<User> userMono = userRepository.findByEmailIgnoreCase(user.getEmail()).log();
        StepVerifier.create(userMono)
                .assertNext(userDB -> assertThat(userDB).isEqualToIgnoringGivenFields(user, "id", "requestedItems", "offeredItems"))
                .verifyComplete();
    }

    @Test
    public void shouldSaveUser1() {
        // given
        User user = buildUser(5);
        clearDB();

        // when
        Flux<User> userFlux = userRepository.saveAll(Flux.just(user));

        //then
        StepVerifier.create(userFlux)
                .assertNext(userDB -> {
                    assertThat(userDB).isEqualToIgnoringGivenFields(user, "id", "requestedItems", "offeredItems");
                    assertThat(userDB.getId()).isNotNull();
                })
                .verifyComplete();
    }


    @Test
    public void checkIfUserEmailExists() {
        // given
        User user = buildUser(5).withFirstName("hari rajgopal");
        userRepository.save(user).block();

        // when
        Mono<Boolean> booleanMono = userRepository.existsByEmailIgnoreCase(user.getEmail());

        //then
        StepVerifier.create(booleanMono)
                .expectNext(true)
                .verifyComplete();
    }

    private void clearDB() {
        userRepository.deleteAll().block();
    }

}
