package box.bookstorebe.repository.common.systemconfig.ex;

import lombok.AllArgsConstructor;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.stereotype.Repository;

@Repository
@AllArgsConstructor
public class SystemConfigExRepositoryImpl implements SystemConfigExRepository {
    private final MongoTemplate mongoTemplate;
}
