package live.goro.covid19.repository;

import live.goro.covid19.domain.User;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest //need to use validators so need entire context
@Slf4j
public class UserRepositoryValidationTest {
    @Autowired
    private UserRepository userRepository;


    @Test
    public void shouldValidateBeforeSaving() {
        // given
        User user = User.builder().build();

        // when
        Mono<User> saveMono = userRepository.save(user);

        //then
        StepVerifier.create(saveMono)
                .expectError()
                .verify();
    }
}
