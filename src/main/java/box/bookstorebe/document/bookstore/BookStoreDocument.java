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

    @Field("address")
    private List<Address> address;

    @Field("other_information")
    private List<OtherInformation> otherInformation;

    @Field("created_at")
    private ZonedDateTime createdAt;

    @Field("updated_at")
    private ZonedDateTime updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Address {
        private String type; // Street, Province, v.v
        private String value;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class OtherInformation {
        private String type;
        private String value;
    }

}
