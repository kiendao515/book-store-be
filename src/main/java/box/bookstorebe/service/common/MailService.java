package box.bookstorebe.service.common;

import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class MailService {

    public SimpleMailMessage constructResetTokenEmail(String baseUrl, String token, UserDocument user, String message) {
        final String url = baseUrl + "/reset-password?id=" + user.getId() + "&token=" + token;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Reset Password");
        email.setText(message + " \r\n" + url);
        return email;
    }

    public SimpleMailMessage constructEmailMessage(OnRegistrationCompleteEvent event, UserDocument user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Complete Registration";
        String confirmationUrl = event.getAppUrl() + "/registration/confirm?token=" + token;
        String message = "To confirm your account, please click the link below:\n" + confirmationUrl;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }
}
