package live.goro.covid19.repository;

import live.goro.covid19.domain.OfferedItemRequest;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OfferedItemRequestRepository extends ReactiveMongoRepository<OfferedItemRequest, String> {

    Flux<OfferedItemRequest> findAllByUserId(String userId);

    Flux<OfferedItemRequest> findByItemOwnerNotifiedOnNull();
}
