package box.bookstorebe.repository.common.webcontent.ex;

import box.bookstorebe.document.common.WebContentDocument;
import org.springframework.data.domain.Page;

public interface WebContentExRepository {
    Page<WebContentDocument> getWebContents(Integer page, Integer size);
}
