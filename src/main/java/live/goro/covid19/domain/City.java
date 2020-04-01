package live.goro.covid19.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "cities")
@Builder
@With
@Value
public class City {
    @Id
    private String id;

    private final String network;

    private final Long geonameId;
}