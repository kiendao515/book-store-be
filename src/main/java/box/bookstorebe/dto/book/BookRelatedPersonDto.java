package box.bookstorebe.dto.book;

import box.bookstorebe.document.book.BookRelatedPersonDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookRelatedPersonDto {
    private String id;
    private String name;
    private String type;
    private List<BookRelatedPersonDocument.Description> descriptions;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
