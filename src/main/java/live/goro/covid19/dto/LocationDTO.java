package live.goro.covid19.dto;

import lombok.Builder;
import lombok.Value;
import lombok.With;

@Builder
@Value
@With
public class LocationDTO {
    String city;
    String country;
    String zip;
    Boolean selected;
}


