package box.bookstorebe.document.settle;

import box.bookstorebe.common.Const;
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
@Document("settlement_detail")
public class SettlementDetail {
    @Id
    private String id;

    @Field("order_item_id")
    private String orderItemId;

    @Field("store_id")
    private String storeId;

    @Field("status")
    private Integer settledStatus; // 0 là chưa chốt, 1 là đã chốt

    @Field("settled_at")
    private ZonedDateTime settledAt;
}
