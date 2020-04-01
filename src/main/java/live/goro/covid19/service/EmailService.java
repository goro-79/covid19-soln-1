package live.goro.covid19.service;

import com.samskivert.mustache.Mustache;
import com.samskivert.mustache.Template;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
@Slf4j
@RequiredArgsConstructor
public class EmailService {
    public static final String EMAIL = "email/";
    private final JavaMailSender sender;
    private final Mustache.Compiler mustache;

    public boolean sendEmail(@NonNull String to,
                          @NonNull String subject,
                          @NonNull String templateName,
                          @NonNull Map<String, Object> model) {
        try {
            MimeMessageHelper helper = new MimeMessageHelper(sender.createMimeMessage());
            helper.setTo(to);
            helper.setSubject(subject);
            helper.setText(renderBody(templateName, model), true);
            sender.send(helper.getMimeMessage());
            log.info("Email send successfully, subject {} , to {}", subject, to);
            return true;
        } catch (Exception ex) {
            log.warn("Failed to send email with subject {}, due to {}", subject, ex.getMessage(), ex);
            return false;
        }
    }

    private String renderBody(String templateName, Map<String, Object> model) {
        Template template = mustache.loadTemplate(EMAIL + templateName);
        String body = template.execute(model);
        log.info("Email body rendered - {}", body);
        return body;
    }
}
