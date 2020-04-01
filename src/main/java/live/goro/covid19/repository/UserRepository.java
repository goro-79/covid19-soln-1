package live.goro.covid19.repository;

import live.goro.covid19.domain.User;
import lombok.NonNull;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Mono;

public interface UserRepository extends ReactiveMongoRepository<User, String> {
    Mono<User> findByEmailIgnoreCase(@NonNull String email);

    Mono<Boolean> existsByEmailIgnoreCase(@NonNull String email);
}
