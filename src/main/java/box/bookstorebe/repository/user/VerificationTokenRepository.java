package box.bookstorebe.repository.user;

import box.bookstorebe.document.user.VerificationToken;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface VerificationTokenRepository extends MongoRepository<VerificationToken, String> {
    VerificationToken findByToken(String token);
}
