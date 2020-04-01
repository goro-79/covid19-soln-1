package live.goro.covid19.service;

import live.goro.covid19.domain.OfferedItem;
import live.goro.covid19.domain.OfferedItemRequest;
import live.goro.covid19.domain.User;
import live.goro.covid19.repository.OfferedItemRequestRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.util.Map;
import java.util.Optional;

import static live.goro.covid19.service.OfferedItemRequestService.EmailProperties.SUBJECT;
import static grsdev7.functions.util.CommonFunctions.makeNoun;

@Slf4j
@Service
@RequiredArgsConstructor
public class OfferedItemRequestService {
    public static final String HEROKU_DNS = "https://covid19-lets-help.herokuapp.com";
    private final OfferedItemRequestRepository repository;
    private final UserService userService;
    private final ItemService itemService;
    private final EmailService emailService;

    public Flux<OfferedItemRequest> createRequests(Flux<OfferedItemRequest> newItemRequestFlux) {
        Flux<OfferedItemRequest> savedFlux = repository.saveAll(newItemRequestFlux);
        return
                savedFlux.doOnComplete(() -> emailItemOwner());
    }

    private void emailItemOwner() {
        repository.findByItemOwnerNotifiedOnNull()
                .subscribe(newRequest -> {
                    Mono<OfferedItem> itemMono = itemService.getOfferedItemById(newRequest.getOfferedItemId());
                    itemMono.subscribe(item -> {
                        userService.getUserById(item.getUserId()) // get item owner details
                                .subscribe(itemOwner -> {
                                            userService.getUserById(newRequest.getUserId()).subscribe(requester -> {
                                                Map<String, Object> model = buildEmailModel(item, itemOwner, requester, newRequest);
                                                boolean sent = emailService.sendEmail(itemOwner.getEmail(), SUBJECT, EmailProperties.TEMPLATE_NAME, model);
                                                if (sent) {
                                                    OfferedItemRequest modifiedRequest = newRequest.withItemOwnerNotifiedOn(Instant.now());
                                                    repository.save(modifiedRequest)
                                                            .subscribe(saved -> log.info("OfferedItemRequest updated with owner notification"));
                                                }
                                            });
                                        }
                                );
                    });
                });
    }

    private Map<String, Object> buildEmailModel(OfferedItem item, User owner, User requester, OfferedItemRequest offeredItemRequest) {
        return Map.of(
                "origin", HEROKU_DNS,
                "name", owner.getFirstName().transform(makeNoun()),
                "email", owner.getEmail(),
                "item.name", item.getName(),
                "requester.city", requester.getCity().transform(makeNoun()),
                "request_id", offeredItemRequest.getId()
        );
    }

    public Mono<OfferedItemRequest> patchRequest(String requestId, Mono<Map> patchRequest) {
        return repository.findById(requestId).flatMap(
                request -> {
                    return
                            patchRequest.flatMap(map -> {
                                        String consent = Optional.ofNullable((String) map.get("itemOwnerConsent"))
                                                .orElseThrow(() -> new IllegalArgumentException("Only itemOwnerConsent is supported"));
                                        if (Boolean.valueOf(consent).equals(Boolean.TRUE)) {
                                            log.info("Item owner has given consent for {}", request);
                                            repository.save(request.withItemOwnerConsentOn(Instant.now()))
                                                    .subscribe(item -> {
                                                        log.info("Item Request updated with itemOwnerConsent {}", item);
                                                        sendEmailToRequester(item);
                                                    });
                                            return Mono.empty();
                                        } else {
                                            throw new IllegalArgumentException("Item owner consent should be true");
                                        }
                                    }

                            );
                }
        );
    }

    private void sendEmailToRequester(OfferedItemRequest request) {
        itemService.getOfferedItemById(request.getOfferedItemId()).log()
                .subscribe(
                        item -> {
                            String itemName = item.getName();
                            userService.getUserById(item.getUserId()).subscribe(
                                    owner -> {
                                        userService.getUserById(request.getUserId()).subscribe(
                                                requester -> {
                                                    String ownerEmail = owner.getEmail();
                                                    Map<String, Object> model = Map.of(
                                                            "item.name", itemName,
                                                            "owner.email", ownerEmail
                                                    );
                                                    boolean sent = emailService.sendEmail(requester.getEmail(), RequestAccepted.SUBJECT,
                                                            RequestAccepted.TEMPLATE_NAME, model);
                                                    if (sent) {
                                                        log.info("Email sent to requester");
                                                    }
                                                }
                                        );
                                    }
                            );

                        }
                );
    }

    public interface EmailProperties {
        String SUBJECT = "Your help is needed by someone";
        String TEMPLATE_NAME = "offered-item-requested";
    }

    public interface RequestAccepted {
        String SUBJECT = "Your have got response";
        String TEMPLATE_NAME = "request-accepted";
    }
}
