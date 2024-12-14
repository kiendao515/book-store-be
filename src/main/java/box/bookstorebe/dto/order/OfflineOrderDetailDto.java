package box.bookstorebe.dto.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OfflineOrderDetailDto {
    private String id;
    private Seller seller;
    private BigDecimal totalBookPrice;
    private int billDiscount;
    private BigDecimal bookDiscount;
    private BigDecimal totalPrice;
    private List<OfflineOrderDetail> details;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Seller {
        private String id;
        private String name;
        private String role;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OfflineOrderDetail {
        private String bookId;
        private String barcode;
        private String bookName;
        private String author;
        private String type;
        private int quantity;
        private BigDecimal price;
        private int discount;
        private BigDecimal totalPrice;
        private String note;
        private ZonedDateTime createdAt;
        private ZonedDateTime updatedAt;
    }
}
