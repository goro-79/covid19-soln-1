package live.goro.covid19.utils;

import grsdev7.functions.util.CommonFunctions;
import live.goro.common.security.security.mongodb.UserCredential;
import live.goro.common.web.functions.object.ObjectFunctions;
import live.goro.covid19.domain.*;
import live.goro.covid19.dto.ItemDTO;
import live.goro.covid19.dto.UserCreationDTO;
import live.goro.covid19.dto.UserDTO;
import lombok.experimental.UtilityClass;
import reactor.core.publisher.Flux;

import javax.validation.constraints.Null;
import java.time.Instant;

import static reactor.core.publisher.Flux.just;

@UtilityClass
public class TestUtil {

    public static User buildUser(int size) {
        return
                User.builder()
                        .firstName("harin")
                        .lastName("test")
                        .city("delhi")
                        .country("india")
                        .email("hari.test@email.com")
                        .zip("12345")
                        .build();
    }

    public static User.UserBuilder userBuilder() {
        return
                User.builder()
                        .firstName("harin")
                        .lastName("test")
                        .city("delhi")
                        .country("india")
                        .email("hari.test@email.com")
                        .zip("12345");

    }

    public static UserDTO buildUserDTO(int size) {
        return ObjectFunctions.createCopy(buildUser(size), UserDTO.class).block();
    }

    public static UserCredential buildUserCredential() {
        return UserCredential.builder()
                .email("hari@mail.com")
                .password("123")
                .build();
    }

    public static UserCreationDTO buildUserCreationDTO() {
        return UserCreationDTO.builder()
                .firstName("harin")
                .lastName("test")
                .city("delhi")
                .country("india")
                .email("hari.test@email.com")
                .zip("12345")
                .password("pas123#")
                .build();
    }


    public static RequestedItem buildRequestedItem(@Null Status status) {
        return RequestedItem.builder()
                .dueDate(null)
                .id(CommonFunctions.randomUUID())
                .userId(CommonFunctions.randomUUID())
                .name("face mask")
                .quantity(200)
                .status(status)
                .createdOn(Instant.now())
                .build();
    }

    public static OfferedItem buildOfferedItem(String userId, Status status) {
        return OfferedItem.builder()
                .dueDate(null)
                .id(CommonFunctions.randomUUID())
                .userId(userId)
                .name("water")
                .quantity(291)
                .status(status)
                .createdOn(Instant.now())
                .build();
    }

    public static OfferedItem.OfferedItemBuilder<?, ?> offeredItemBuilder(String userId, Status status) {
        return OfferedItem.builder()
                .dueDate(null)
                .id(CommonFunctions.randomUUID())
                .userId(userId)
                .name("water")
                .quantity(291)
                .status(status)
                .createdOn(Instant.now());
    }

    public static boolean validateItem(AbstractItem item) {
        return item != null &&
                item.getId() != null &&
                item.getName() != null &&
                item.getQuantity() != null &&
                item.getStatus() != null &&
                item.getCreatedOn() != null;
    }

    public static UserDTO buildNewUserDTO() {
        User user = buildUser(0);
        return ObjectFunctions.createCopy(user, UserDTO.class).block();
    }

    public static Flux<RequestedItem> buildRequestedItems() {
        return just(buildRequestedItem(Status.OPEN), buildRequestedItem(Status.OPEN), buildRequestedItem(Status.OPEN));
    }

    public static Flux<OfferedItem> buildOfferedItems() {
        OfferedItem offeredItem = OfferedItem.builder()
                .dueDate(null)
                .id(CommonFunctions.randomUUID())
                .name("face mask")
                .quantity(200)
                .status(Status.OPEN)
                .createdOn(Instant.now())
                .build();
        return just(offeredItem, offeredItem, offeredItem);
    }

    public static ItemDTO buildNewItemDTO() {
        return ItemDTO.builder()
                .name("apples")
                .quantity(20)
                .build();
    }

    public static OfferedItemRequest.OfferedItemRequestBuilder offeredItemRequestBuilder() {
        return OfferedItemRequest.builder()
                .userId(CommonFunctions.randomUUID())
                .offeredItemId(CommonFunctions.randomUUID());
    }
}
