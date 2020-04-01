package live.goro.covid19.domain;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import java.time.Instant;

@SuperBuilder
@Getter
@With
@AllArgsConstructor
@ToString
public class AbstractItem {
    @Id
    private String id;

    @NotBlank
    private String userId; // maps to users collection

    @NotBlank
    private String name;

    @Min(0)
    private Integer quantity;

    @JsonFormat(pattern = "dd-MM-yy", timezone = "UTC")
    private Instant dueDate;

    @NotNull
    private Status status;

    @NotNull
    @CreatedDate
    private Instant createdOn; //todo verify should get auto populated by spring audit

    public AbstractItem() {

    }

    public enum ItemType {
        OFFERED, REQUESTED;

        public String toString() {
            return name().toString();
        }

        public static ItemType from(String name) {
            return Enum.valueOf(ItemType.class, name.toUpperCase());
        }
    }
}


