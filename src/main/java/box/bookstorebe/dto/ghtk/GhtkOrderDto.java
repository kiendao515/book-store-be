package box.bookstorebe.dto.ghtk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class GhtkOrderDto {
    private boolean success;
    private String message;
    private OrderResult order;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrderResult {
        private String partnerId;
        private String label;
        private Integer area;
        private Long fee;
        private Long insuranceFee;
        private String estimatedPickTime;
        private String estimatedDeliverTime;
        private Integer statusId;
        private Long trackingId;
        private String sortingCode;
        private String dateToDelayPick;
        private Integer pickWorkShift;
        private String dateToDelayDeliver;
        private Integer deliverWorkShift;
        private Integer pkgDraftId;
        private Integer isXfast;
    }
}
