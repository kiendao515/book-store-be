package box.bookstorebe.document.account;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("stores")
public class StoreDocument {
    @Id
    private String id;

    @Field(name = "account_id")
    private String accountId;

    @Field(name = "store_name")
    private String storeName;

    @Field(name = "description")
    private String description;

    @Field(name = "phone_number")
    private String phoneNumber;

    @Field(name = "address")
    private String address;

    @Field(name = "thumbnail")
    private String thumbnail;
}