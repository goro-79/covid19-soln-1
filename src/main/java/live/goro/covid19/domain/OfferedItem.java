package live.goro.covid19.domain;

import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.mongodb.core.mapping.Document;

@ToString(callSuper = true)
@Document(collection = "offered_items")
@Value
@SuperBuilder
public class OfferedItem extends AbstractItem {

    public OfferedItem() {
        super();
    }
}

