package live.goro.covid19.service;

import live.goro.common.security.security.mongodb.CommonUserDetailsService;
import live.goro.common.security.security.mongodb.UserCredential;
import live.goro.covid19.dto.UserDTO;
import live.goro.covid19.dto.UserCreationDTO;
import live.goro.covid19.repository.UserRepository;
import live.goro.covid19.domain.User;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.ReactiveUserDetailsService;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

import static java.util.stream.Collectors.joining;
import static reactor.core.publisher.Mono.just;

@Service
@Slf4j
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final CommonUserDetailsService credentialService;
    private final ReactiveUserDetailsService userDetailsService;

    public Mono<UserDTO> getUserByEmail(@NonNull String email) {
        Mono<User> userMono = userRepository.findByEmailIgnoreCase(email);
        return userMono.map(UserDTO::from);
    }


    public Mono<UserDTO> createUser(@NonNull UserCreationDTO userCreationDTO) {
        User user = userCreationDTO.toUser();
        return userRepository.save(user)
                .flatMap(saved -> {
                    Mono<UserCredential> savedCredential = credentialService.save(saved.getEmail(), userCreationDTO.getPassword());
                    final Mono<User> savedUserMono = savedCredential.map(value -> saved);
                    return savedUserMono.flatMap(value -> just(UserDTO.from(value)));
                });
    }

    public Mono<Boolean> existsByEmail(String email) {
        return userRepository.existsByEmailIgnoreCase(email);
    }

    public Mono<User> getUserById(String userId) {
        return userRepository.findById(userId);
    }

}
