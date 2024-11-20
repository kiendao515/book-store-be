package box.bookstorebe.repository.common.province;

import box.bookstorebe.document.common.ProvinceDocument;
import box.bookstorebe.repository.common.province.ex.ProvinceExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface ProvinceRepository extends MongoRepository<ProvinceDocument, String>, ProvinceExRepository {

}
