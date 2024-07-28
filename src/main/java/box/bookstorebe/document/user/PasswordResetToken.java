package box.bookstorebe.document.user;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

@Data
@Document("password_reset_tokens")
public class PasswordResetToken {
    private static final int EXPIRATION = 60 * 24;

    @Id
    private String id;

    @Field(name = "token")
    private String token;

    @Field(name = "user_id")
    @NotNull
    private String userId;

    @Field(name = "expiry_date")
    private ZonedDateTime expiryDate;

    private ZonedDateTime calculateExpiryDate(int expiryTimeInMinutes) {
        return ZonedDateTime.now().plusMinutes(expiryTimeInMinutes);
    }

    public PasswordResetToken(String token, String userId) {
        this.token = token;
        this.userId = userId;
        this.expiryDate = calculateExpiryDate(EXPIRATION);
    }
}
