package box.bookstorebe.repository.user;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.repository.user.ex.AccountExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface AccountRepository extends MongoRepository<AccountDocument, String>, AccountExRepository {
    Optional<AccountDocument> findByEmail(String email);
    Optional<AccountDocument> findByToken(String token);
}
