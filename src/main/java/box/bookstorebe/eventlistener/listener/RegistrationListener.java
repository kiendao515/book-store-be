package box.bookstorebe.eventlistener.listener;

import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import box.bookstorebe.service.auth.AuthService;
import box.bookstorebe.service.common.MailService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Slf4j
@Service
@Component
public class RegistrationListener {
    private final AuthService authService;
    private final JavaMailSender mailSender;
    private final MailService mailService;

    public RegistrationListener(AuthService authService, JavaMailSender mailSender, MailService mailService) {
        this.mailSender = mailSender;
        this.authService = authService;
        this.mailService = mailService;
    }

    @EventListener
    @Async
    void handleEvent(OnRegistrationCompleteEvent event) {
        try {
            UserDocument user = event.getUser();
            String token = UUID.randomUUID().toString();
            this.authService.createVerificationTokenForUser(user, token);
            SimpleMailMessage email = mailService.constructEmailMessage(event, user, token);
            mailSender.send(email);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

}
