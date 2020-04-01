package live.goro.covid19.service;

import lombok.NonNull;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.Map;

@SpringBootTest
public class EmailServiceTest {

    @Autowired
    private EmailService emailService;

    @Test
    void sendEmail_test01() {
        String to = "grsdev7@gmail.com";
        String subject = "Test Subject";
        @NonNull Map<String, Object> model = Map.of(
                "name", "Janu",
                "item.name", "toothpaste",
                "requester.city", "Delhi"
        );
        emailService.sendEmail(to, subject, "offered-item-requested", model);
    }
}
