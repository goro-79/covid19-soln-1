package live.goro.covid19.dto;

import live.goro.covid19.domain.User;
import lombok.Builder;
import lombok.Value;
import lombok.With;

@Value
@With
@Builder
public class UserDTO {
    String id;

    String email;

    String firstName;

    String lastName;

    String country;

    String city;

    String zip;

    public static UserDTO from(User user) {
        return
                new UserDTO(
                        user.getId(),
                        user.getEmail(),
                        user.getFirstName(),
                        user.getLastName(),
                        user.getCountry(),
                        user.getCity(),
                        user.getZip()
                );
    }
}
