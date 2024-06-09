package box.bookstorebe.repository.user;

import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.repository.user.ex.UserExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface UserRepository extends MongoRepository<UserDocument, String>, UserExRepository {
    Optional<UserDocument> findByEmail(String email);
}
