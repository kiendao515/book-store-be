package box.bookstorebe.service.common;

import box.bookstorebe.document.order.OrderDocument;
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
        final String url = baseUrl + "/reset-password?token=" + token;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Reset Password");
        email.setText(message + " \r\n" + url);
        return email;
    }

    public SimpleMailMessage constructEmailMessage(OnRegistrationCompleteEvent event, UserDocument user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Complete Registration";
        String confirmationUrl = event.getAppUrl() + "/confirm-registration?token=" + token;
        String message = "To confirm your account, please click the link below:\n" + confirmationUrl;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }
    public SimpleMailMessage sendMailOrderDetail(String to,OrderDocument orderDocument) {
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(to);
        email.setSubject("Order Details - " + orderDocument.getOrderId());
        email.setText(formatOrderDetails(orderDocument));
        return email;
    }
    private String formatOrderDetails(OrderDocument order) {
        StringBuilder builder = new StringBuilder();

        builder.append("Order ID: ").append(order.getOrderId()).append("\n");
        builder.append("Customer Name: ").append(order.getCustomerName()).append("\n");
        builder.append("Customer Phone: ").append(order.getCustomerPhone()).append("\n");
        builder.append("Email: ").append(order.getEmail()).append("\n");
        builder.append("Address: ").append(order.getAddress()).append("\n");
        builder.append("Status: ").append(order.getStatus()).append("\n");
        builder.append("Payment Type: ").append(order.isPaymentType() ? "Paid by Wallet" : "COD").append("\n");
        builder.append("Created At: ").append(order.getCreatedAt()).append("\n");
        builder.append("Updated At: ").append(order.getUpdatedAt()).append("\n");

        builder.append("\nBooks Ordered:\n");

        order.getItems().forEach(book -> {
            builder.append("\nBook ID: ").append(book.getId()).append("\n");
            builder.append("Price: ").append(book.getPrice()).append("\n");
            builder.append("Status: ").append(book.getStatus()).append("\n");

            if (book.getDescriptions() != null && !book.getDescriptions().isEmpty()) {
                builder.append("Descriptions:\n");
                book.getDescriptions().forEach(description ->
                        builder.append("  - ").append(description.getType()).append(": ").append(description.getValue()).append("\n"));
            }

            if (book.getRelatedPeople() != null && !book.getRelatedPeople().isEmpty()) {
                builder.append("Related People:\n");
                book.getRelatedPeople().forEach(person ->
                        builder.append("  - ").append(person.getType()).append(": ").append(person.getType()).append("\n"));
            }

            if (book.getRelatedImages() != null && !book.getRelatedImages().isEmpty()) {
                builder.append("Related Images:\n");
                book.getRelatedImages().forEach(image ->
                        builder.append("  - ").append(image.getType()).append(": ").append(image.getImageId()).append("\n"));
            }
        });

        return builder.toString();
    }
}
