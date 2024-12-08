package box.bookstorebe.dto.report;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderReportDto {
    private Statistic readyToConfirm;
    private Statistic readyToPackage;
    private Statistic readyToShip;
    private Statistic shipping;
    private Statistic done;
    private Statistic cancel;

    @AllArgsConstructor
    @NoArgsConstructor
    @Data
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class Statistic {
        private Integer quantity;
        private float percentChange;
        private BigDecimal amount;

    }
}
