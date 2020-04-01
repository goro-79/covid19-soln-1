package live.goro.covid19.repository;

import live.goro.covid19.domain.IP4Block;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface IP4BlockRepository extends ReactiveMongoRepository<IP4Block, String> {

    Flux<IP4Block> findByNetwork(String ipAddress);
}
