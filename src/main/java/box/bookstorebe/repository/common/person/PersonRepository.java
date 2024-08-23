package box.bookstorebe.repository.common.person;

import box.bookstorebe.document.common.PersonDocument;
import box.bookstorebe.repository.common.person.ex.PersonExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface PersonRepository extends MongoRepository<PersonDocument, String>, PersonExRepository {
    List<PersonDocument> findByNameStartingWithAndType(String nameStart, String type);
}
