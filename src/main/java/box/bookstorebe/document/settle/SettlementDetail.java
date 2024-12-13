package box.bookstorebe.document.settle;

import box.bookstorebe.common.Const;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

public class SettlementDetail {
    @Id
    private String id;

    @Field("order_item_id")
    private String orderItemId;

    @Field("store_id")
    private String storeId;

    @Field("status")
    private Const.SettlementStatus settledStatus;

    @Field("settled_at")
    private ZonedDateTime settledAt;
}
