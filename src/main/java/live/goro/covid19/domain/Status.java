package live.goro.covid19.domain;

import com.fasterxml.jackson.annotation.JsonProperty;

public enum Status {
    @JsonProperty("open")
    OPEN,
    @JsonProperty("closed")
    CLOSED;
}
