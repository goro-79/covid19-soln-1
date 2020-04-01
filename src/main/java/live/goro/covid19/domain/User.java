package live.goro.covid19.domain;

import lombok.Builder;
import lombok.Value;
import lombok.With;
import lombok.experimental.NonFinal;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.*;

@Value
@With
@Builder
@Document(collection = "users")
public class User {
    @Id
    @NonFinal
    String id;

    @NotBlank
    @Email
    @Size(max = 50) String email;

    @NotBlank
    @Size(max = 50) String firstName;

    @NotBlank
    @Size(max = 50) String lastName;

    @NotBlank
    @Size(max = 50) String country;

    @NotBlank
    @Size(max = 50) String city;

    @NotBlank
    @Size(max = 50)
    String zip;
}

