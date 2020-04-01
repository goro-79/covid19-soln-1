package live.goro.covid19.repository;

import live.goro.common.security.security.mongodb.UserCredential;
import live.goro.common.security.security.mongodb.UserCredentialRepository;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static live.goro.covid19.utils.TestUtil.buildUserCredential;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class UserCredentialRepositoryTest {

    @Autowired
    private UserCredentialRepository userCredentialRepository;

    @Test
    public void shouldSaveUserAndFindCredential() {
        // given
        UserCredential credential = buildUserCredential();

        // when
        userCredentialRepository.save(credential).block();

        // then
        final Mono<UserCredential> savedCredentialMono = userCredentialRepository.findByEmail(credential.getEmail());
        StepVerifier.create(savedCredentialMono)
                .assertNext(savedUser -> assertThat(savedUser).isEqualToIgnoringGivenFields(credential, "id"))
                .verifyComplete();
    }

}
