package box.bookstorebe.model.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateOfflineOrderModel {
    private List<BookOfflineDetail> bookOrders;
    private int billDiscount; // 0 - 100 (%)

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class BookOfflineDetail {
        private String barcode;
        private int quantity;
        private int discount;
        private String note;
    }
}
