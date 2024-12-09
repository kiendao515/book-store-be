package box.bookstorebe.repository.user;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.repository.user.ex.AccountExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.ZonedDateTime;
import java.util.Date;
import java.util.List;
import java.util.Optional;

public interface AccountRepository extends MongoRepository<AccountDocument, String>, AccountExRepository {
    Optional<AccountDocument> findByEmail(String email);
    Optional<AccountDocument> findByToken(String token);
    List<AccountDocument> findAllByCreatedAtBeforeAndEnabledIs(ZonedDateTime createdAt, Integer enabled);
}
