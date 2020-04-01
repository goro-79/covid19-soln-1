package live.goro.covid19.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

/**
 * domain object represents request made to take this item
 */
@Document(collection = "offered_item_requests")
@Builder
@With
@Getter
@EqualsAndHashCode
@ToString
public class OfferedItemRequest {
    @Id
    private String id;

    @NotBlank
    private final String userId;

    @NotBlank
    private final String offeredItemId; //maps to offeredItem

    private final Instant itemOwnerNotifiedOn;

    private final Instant itemOwnerConsentOn;

    @CreatedDate
    private final Instant createdOn; //todo verify should get auto populated by spring audit
}
