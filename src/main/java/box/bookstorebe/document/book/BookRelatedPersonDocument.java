package box.bookstorebe.document.book;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("book_related_people")
public class BookRelatedPersonDocument {
    @Id
    private String id;

    @Field(name = "name")
    private String name;

    @Field(name = "type")
    private String type;

    @Field(name = "descriptions")
    private List<Description> descriptions;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Description {
        private String type;
        private String value;
    }
}
