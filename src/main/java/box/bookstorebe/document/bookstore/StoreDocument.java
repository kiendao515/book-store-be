package box.bookstorebe.document.bookstore;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("stores")
public class StoreDocument {
    @Id
    private String id;

    @Field(name = "account_id")
    private String accountId;

    @Field(name = "name")
    private String name;

    @Field(name = "description")
    private String description;

    @Field(name = "phone_number")
    private String phoneNumber;

    @Field(name = "address")
    private String address;

    @Field(name = "thumbnail")
    private String thumbnail;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Field(name = "deleted_at")
    private ZonedDateTime deletedAt;
}