package box.bookstorebe.repository.common.webcontent;

import box.bookstorebe.document.common.SystemConfigDocument;
import box.bookstorebe.document.common.WebContentDocument;
import box.bookstorebe.repository.common.webcontent.ex.WebContentExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface WebContentRepository extends MongoRepository<WebContentDocument, String>, WebContentExRepository {
    WebContentDocument findByKey(String key);
}
