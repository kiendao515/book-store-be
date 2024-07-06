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
@Document("book_realities")
public class BookRealityDocument {
    @Id
    private String id;

    @Field("price")
    private Double price;

    @Field("status")
    private String status; // "AVAILABLE" or "UNAVAILABLE"

    @Field("type")
    private String type; // "OLD", "NEW"

    @Field("image_ids")
    private List<String> imageIds;

    @Field("book_id")
    private String bookId;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;
}
