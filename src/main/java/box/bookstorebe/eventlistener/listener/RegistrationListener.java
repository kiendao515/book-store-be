package box.bookstorebe.eventlistener.listener;

import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import box.bookstorebe.service.auth.AuthService;
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

    public RegistrationListener(AuthService authService, JavaMailSender mailSender) {
        this.mailSender = mailSender;
        this.authService = authService;
    }

    @EventListener
    @Async
    void handleEvent(OnRegistrationCompleteEvent event) {
        try{
            UserDocument user = event.getUser();
            String token = UUID.randomUUID().toString();
            this.authService.createVerificationTokenForUser(user, token);
            SimpleMailMessage email = this.constructEmailMessage(event, user, token);
            mailSender.send(email);

        } catch (Exception ex) {
            log.error(ex.getMessage());
        }
    }

    private SimpleMailMessage constructEmailMessage(OnRegistrationCompleteEvent event, UserDocument user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Complete Registration";
        String confirmationUrl = event.getAppUrl() + "/registration/confirm?token=" + token;
        String message = "To confirm your account, please click the link below:\n"
                + confirmationUrl;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }
}
