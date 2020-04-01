package live.goro.covid19.repository;

import live.goro.covid19.domain.RequestedItem;
import live.goro.covid19.domain.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static live.goro.covid19.utils.TestUtil.buildRequestedItem;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class RequestedItemRepositoryTest {

    @Autowired
    private RequestedItemRepository repository;

    @Test
    public void save_test01() {
        // given
        RequestedItem requestedItem = buildRequestedItem(Status.OPEN);

        // when
        Mono<RequestedItem> savedMono = repository.save(requestedItem);

        // then
        StepVerifier.create(savedMono)
                .assertNext(saved -> assertThat(saved).isEqualToIgnoringGivenFields(requestedItem, "id"))
                .verifyComplete()
        ;
    }
}
