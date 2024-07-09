package box.bookstorebe.document.order;

import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.user.Role;
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
@Document("orders")
public class OrderDocument {
    @Id
    private String id;

    @Field(name = "address")
    private String address;

    @Field(name = "customer_name")
    private String customerName;

    @Field(name = "customer_phone")
    private String customerPhone;

    @Field(name = "email")
    private String email;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Field(name = "status")
    private String status;

    @Field(name = "items")
    private List<BookRealityDocument> items;

}
