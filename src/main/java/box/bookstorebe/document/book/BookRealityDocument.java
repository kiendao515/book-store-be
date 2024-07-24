package box.bookstorebe.document.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Document("book_realities")
public class BookRealityDocument extends BookCommon {
    @Id
    private String id;

    @Field("price")
    private Double price;

    @Field("status")
    private String status;

    @Field("type")
    private String type; // "OLD", "NEW"

    @Field("book_id")
    private String bookId;
}
