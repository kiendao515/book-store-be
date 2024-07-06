package box.bookstorebe.model.book.bookrelatedperson;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookRelatedPersonDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateBookRelatedPersonModel {
    private String name;
    private List<BookRelatedPersonDocument.Description> descriptions;
    private String type;
}
