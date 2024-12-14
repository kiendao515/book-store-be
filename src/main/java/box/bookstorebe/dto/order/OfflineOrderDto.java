package box.bookstorebe.dto.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OfflineOrderDto {
    private String id;
    private Seller seller;
    private BigDecimal totalBookPrice;
    private int billDiscount;
    private BigDecimal bookDiscount;
    private BigDecimal totalPrice;
    private int quantity;
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
}
