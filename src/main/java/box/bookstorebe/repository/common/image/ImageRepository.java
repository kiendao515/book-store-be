package box.bookstorebe.repository.common.image;

import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.document.common.SystemConfigDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ImageRepository extends MongoRepository<ImageDocument, String> {
}
