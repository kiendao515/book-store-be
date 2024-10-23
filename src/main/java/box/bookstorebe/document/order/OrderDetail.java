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
public class OrderDetail {
    @Id
    private String id;

    @Field(name= "order_id")
    private String orderId;

    @Field(name = "product_id")
    private String productId;

    @Field(name = "type")
    private Enum type;

    @Field(name = "quantity")
    private Integer quantity;
}
