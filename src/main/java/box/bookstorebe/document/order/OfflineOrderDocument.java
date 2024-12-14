package box.bookstorebe.document.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("offline_orders")
public class OfflineOrderDocument {
    @Id
    private String id;

    @Field(name = "account_id") // seller
    private String accountId;

    @Field(name = "book_store_id")
    private String bookStoreId;

    @Field(name = "total_book_price")
    private BigDecimal totalBookPrice;

    @Field(name = "book_discount")
    private BigDecimal bookDiscount;

    @Field(name = "bill_discount")
    private int billDiscount;

    @Field(name = "total_price")
    private BigDecimal totalPrice;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;
}
