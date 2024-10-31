package box.bookstorebe.document.booksearchrequest;

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
@Document("book_search_requests")
public class BookSearchRequestDocument {
    @Id
    private String id;

    @Field
    private String userId;

    @Field("full_name")
    private String fullName;

    @Field("email")
    private String email;

    @Field("phone_number")
    private String phoneNumber;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;
}
