package live.goro.covid19.dto;

import live.goro.covid19.domain.User;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class UserCreationDTO {
    private String email;

    private String firstName;

    private String lastName;

    private String country;

    private String city;

    private String zip;

    private String password;

    public User toUser() {
        return
                User.builder()
                        .email(this.email)
                        .firstName(firstName)
                        .lastName(lastName)
                        .country(country)
                        .city(city)
                        .zip(zip)
                        .build();
    }
}
