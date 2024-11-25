package box.bookstorebe.document.book;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("book_favorites")
public class BookFavoriteDocument {
    @Id
    private String id;

    @Field("book_id")
    private String bookId;

    @Field("account_id")
    private String userId;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;
}
