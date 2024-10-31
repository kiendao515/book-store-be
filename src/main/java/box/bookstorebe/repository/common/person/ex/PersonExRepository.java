package box.bookstorebe.repository.common.person.ex;

import box.bookstorebe.document.common.PersonDocument;
import org.springframework.data.domain.Page;

public interface PersonExRepository {
    Page<PersonDocument> getPeople(String name, String type, Integer page, Integer size);
}
