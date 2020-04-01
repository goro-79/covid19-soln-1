package live.goro.covid19.service;

import live.goro.covid19.domain.OfferedItem;
import live.goro.covid19.domain.OfferedItemRequest;
import live.goro.covid19.domain.Status;
import live.goro.covid19.domain.User;
import live.goro.covid19.repository.OfferedItemRequestRepository;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static live.goro.covid19.utils.TestUtil.offeredItemBuilder;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
import static reactor.core.publisher.Mono.just;

@ExtendWith(MockitoExtension.class)
public class OfferedItemRequestServiceTest {
    @InjectMocks
    private OfferedItemRequestService service;
    @Mock
    private OfferedItemRequestRepository repository;
    @Mock
    private EmailService emailService;
    @Mock
    private ItemService itemService;

    @Mock
    private UserService userService;

/*    @Test //todo check why stubbing is failing
    void createItems_test01() {
        // given
        User owner = userBuilder().id(randomUUID()).build();
        mockGetUser(owner);
        OfferedItem offeredItem = buildOfferedItem(owner, randomUUID());
        mockGetOfferedItem(offeredItem);
        User requester = userBuilder().id(randomUUID()).build();
        mockGetUser(requester);
        OfferedItemRequest itemRequest = buildOfferedItemRequest(requester.getId(), offeredItem.getId());
        String offeredItemRequestId = randomUUID();
        given(repository.saveAll(Flux.just(itemRequest))).willReturn(Flux.just(itemRequest.withId(offeredItemRequestId)));

        // when
        Flux<OfferedItemRequest> savedRequestsFlux = service.createRequests(Flux.just(itemRequest));

        // then
        StepVerifier.create(savedRequestsFlux)
                .assertNext(saved -> {
                    Assertions.assertThat(saved).isEqualToIgnoringNullFields(itemRequest);
                })
                .verifyComplete();

        Map<String, Object> model = Map.of(
                "name", owner.getFirstName().transform(makeNoun()),
                "item.name", offeredItem.getName(),
                "requester.city", requester.getCity().transform(makeNoun()),
                "request_id", offeredItemRequestId
        );
        BDDMockito.verify(emailService, times(1)).sendEmail(owner.getEmail(), SUBJECT, TEMPLATE_NAME, model);
    }*/

    private void mockGetOfferedItem(OfferedItem offeredItem) {
        given(itemService.getOfferedItemById(offeredItem.getId())).willReturn(just(offeredItem));
    }

    private OfferedItem buildOfferedItem(User owner, String id) {
        return offeredItemBuilder(owner.getId(), Status.OPEN)
                .id(id)
                .build();
    }

    private void mockGetUser(User requester) {
        given(userService.getUserById(requester.getId())).willReturn(just(requester));
    }

    private OfferedItemRequest buildOfferedItemRequest(String userId, String offeredItemId) {
        return OfferedItemRequest.builder()
                .userId(userId)
                .offeredItemId(offeredItemId)
                .build();
    }
}
