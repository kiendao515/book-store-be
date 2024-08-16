package box.bookstorebe.repository.book;

import box.bookstorebe.document.common.CommonEntity;
import org.springframework.data.mongodb.repository.MongoRepository;
import box.bookstorebe.repository.book.ex.CommonEntityExRepository;
public interface CommonEntityRepository extends MongoRepository<CommonEntity, String>, CommonEntityExRepository {
}
