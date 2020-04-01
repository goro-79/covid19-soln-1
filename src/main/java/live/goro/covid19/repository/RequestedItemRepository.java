package live.goro.covid19.repository;

import live.goro.covid19.domain.RequestedItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface RequestedItemRepository extends ReactiveMongoRepository<RequestedItem, String> {

    Flux<RequestedItem> findAllByUserId(String userId);
}
