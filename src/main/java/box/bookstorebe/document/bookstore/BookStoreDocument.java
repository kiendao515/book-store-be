package box.bookstorebe.document.bookstore;

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
@Document("book_stores")
public class BookStoreDocument {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("image_id")
    private String imageId;

    @Field("address")
    private String address;

//    @Field("description")
//    private String description;

    @Field("phone_number")
    private String phoneNumber;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;


}
