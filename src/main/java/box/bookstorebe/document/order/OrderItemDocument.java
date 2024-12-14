package box.bookstorebe.document.order;

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
@Document("order_items")
public class OrderItemDocument {
    @Id
    private String id;

    @Field(name= "order_id")
    private String orderId;

    @Field(name = "book_inventory_id")
    private String bookInventoryId;

    @Field(name = "quantity")
    private Integer quantity;

    @Field("status")
    private Integer settledStatus; // 0 là chưa chốt, 1 là đã chốt
}
