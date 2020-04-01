package live.goro.covid19.service;

import live.goro.common.security.security.mongodb.CommonUserDetailsService;
import live.goro.common.security.security.mongodb.UserCredential;
import live.goro.common.security.security.mongodb.UserCredentialRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;
import reactor.core.publisher.Mono;

import static org.mockito.BDDMockito.*;


@ExtendWith(MockitoExtension.class)
public class CommonUserDetailsServiceTest {
    @InjectMocks
    private CommonUserDetailsService commonUserDetailsService;

    @Mock
    private UserCredentialRepository repository;

    @Mock
    private PasswordEncoder encoder;

    @Test
    public void shouldSaveCredential() {
        // given
        String email = "hari@hari.com", password = "abc2#";
        given(encoder.encode(password)).willReturn("encode12839njkc");
        UserCredential credential = UserCredential.builder()
                .email(email)
                .password(encoder.encode(password))
                .build();

        // when
        @SuppressWarnings("unused")
        final Mono<UserCredential> credentialMono = commonUserDetailsService.save(email, password);

        // then
        verify(repository, times(1)).save(credential);
    }

    @Test
    public void shouldFindCredential() {
        // given
        String email = "hari@hari.com";

        // when
        @SuppressWarnings("unused") final Mono<UserCredential> credentialMono = commonUserDetailsService.findByEmail(email);

        // then
        verify(repository, times(1)).findByEmail(email);
    }
}
