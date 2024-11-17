package box.bookstorebe.document.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("order_items")
public class OrderItem {
    @Id
    private String id;

    @Field(name= "order_id")
    private String orderId;

    @Field(name = "book_inventory_id")
    private String bookInventoryId;

    @Field(name = "quantity")
    private Integer quantity;
}
