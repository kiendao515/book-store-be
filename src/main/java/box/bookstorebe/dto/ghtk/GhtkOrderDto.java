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
    private OrderResult data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class OrderResult {
        private String labelId;
        private String partnerId;
        private String orderId;
        private String created;
        private String modified;
        private String message;
        private String pickDate;
        private String deliverDate;
        private Long shipMoney;
        private Long insurance;
        private Long value;
        private Integer weight;
        private Long pickMoney;
        private Long isFreeship;
    }
}
