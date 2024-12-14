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
@Document("offline_order_details")
public class OfflineOrderDetailDocument {
    @Id
    private String id;

    @Field(name = "offline_order_id")
    private String offlineOrderId;

    @Field(name = "book_inventory_id")
    private String bookInventoryId;

    @Field(name = "quantity")
    private int quantity;

    @Field(name = "discount")
    private int discount;

    @Field(name = "note")
    private String note;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;
}
