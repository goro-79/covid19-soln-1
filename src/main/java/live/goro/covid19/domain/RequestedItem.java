package live.goro.covid19.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@Value
@With
@SuperBuilder
@Document(collection = "requested_items")
public class RequestedItem extends AbstractItem {

    public RequestedItem() {
    }
}
