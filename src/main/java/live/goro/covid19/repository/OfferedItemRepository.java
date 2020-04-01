package live.goro.covid19.repository;

import live.goro.covid19.domain.OfferedItem;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;
import reactor.core.publisher.Flux;

public interface OfferedItemRepository extends ReactiveMongoRepository<OfferedItem, String> {

    Flux<OfferedItem> findAllByUserId(String userId);

    Flux<OfferedItem> findAllByUserIdNotAndNameContainingOrderByCreatedOnDesc(String userId, String name);
}
