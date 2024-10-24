package box.bookstorebe.repository.user;


import box.bookstorebe.document.account.PasswordResetToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PasswordResetTokenRepository extends MongoRepository<PasswordResetToken, String> {
    PasswordResetToken findByToken(String token);
}
