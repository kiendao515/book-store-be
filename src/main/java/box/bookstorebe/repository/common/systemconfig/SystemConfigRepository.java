package box.bookstorebe.repository.common.systemconfig;

import box.bookstorebe.document.common.SystemConfigDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface SystemConfigRepository extends MongoRepository<SystemConfigDocument, String> {
}
