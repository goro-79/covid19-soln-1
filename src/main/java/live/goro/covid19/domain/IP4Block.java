package live.goro.covid19.domain;

import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotBlank;
import java.time.Instant;

@Document(collection = "ip4_blocks")
@Builder
@With
@Value
public class IP4Block {
    @Id
    private String id;

    private final String network;

    private final Long geonameId;
}