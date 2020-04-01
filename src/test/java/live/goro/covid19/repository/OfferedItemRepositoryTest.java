package live.goro.covid19.repository;

import live.goro.covid19.domain.OfferedItem;
import live.goro.covid19.utils.TestUtil;
import live.goro.covid19.domain.Status;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.data.mongo.DataMongoTest;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.test.StepVerifier;

import static live.goro.covid19.utils.TestUtil.buildOfferedItem;
import static grsdev7.functions.util.CommonFunctions.randomUUID;
import static org.assertj.core.api.Assertions.assertThat;

@DataMongoTest
public class OfferedItemRepositoryTest {

    @Autowired
    private OfferedItemRepository repository;

    @Test
    public void save_test01() {
        // given
        OfferedItem item = TestUtil.buildOfferedItem(randomUUID(), Status.OPEN);

        // when
        Mono<OfferedItem> savedMono = repository.save(item);

        // then
        StepVerifier.create(savedMono)
                .assertNext(saved -> assertThat(saved).isEqualToIgnoringGivenFields(item, "id"))
                .verifyComplete()
        ;
    }

    @Test
    public void save_test02() {
        // given
        OfferedItem inputItem = TestUtil.buildOfferedItem(randomUUID(), Status.OPEN);

        // when
        Flux<OfferedItem> saved = repository.saveAll(Flux.just(inputItem));

        // then
        StepVerifier.create(saved)
                .assertNext(value -> {
                    assertThat(value).isEqualToIgnoringGivenFields(inputItem, "id");
                    assertThat(value.getId()).isNotNull();
                })
                .verifyComplete()
        ;
    }

    @Test
    public void findByQuery_test01() {
        // given
        String userId1 = randomUUID();
        String userId2 = randomUUID();
        OfferedItem item1 = buildOfferedItem(userId1, Status.OPEN);
        OfferedItem item2 = buildOfferedItem(userId2, Status.OPEN);
        repository.saveAll(Flux.just(item1, item2)).blockLast();

        // when
        Flux<OfferedItem> items = repository.findAllByUserIdNotAndNameContainingOrderByCreatedOnDesc(userId2, item1.getName());

        // then
        StepVerifier.create(items)
                .assertNext(item -> {
                    assertThat(item.getUserId()).isNotEqualTo(userId2);
                    assertThat(item.getName()).isEqualTo(item1.getName());
                })
                .verifyComplete();

    }
}
