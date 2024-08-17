package box.bookstorebe.repository.book.ex;

import box.bookstorebe.document.common.CommonEntity;
import org.springframework.data.domain.Page;

public interface CommonEntityExRepository {
    Page<CommonEntity> getCommonEntity(String type, Integer page, Integer size);

}
