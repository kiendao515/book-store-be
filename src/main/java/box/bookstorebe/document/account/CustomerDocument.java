package box.bookstorebe.document.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("customers")
public class CustomerDocument {
    @Id
    private String id;

    @Field(name = "account_id")
    private String accountId;

    @Field(name = "name")
    private String name;

    @Field(name = "phone_number")
    private String phoneNumber;

    @Field(name = "avatar")
    private String avatar;

    @Field(name = "date_of_birth")
    private ZonedDateTime dateOfBirth;

    @Field(name = "address")
    private List<String> address;

    @Field(name = "point")
    private Float point = 0.0f;

    @Field(name = "received_award")
    private Integer receivedAward = 0;

    @Field(name = "deleted_at")
    private ZonedDateTime deletedAt;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;
}
