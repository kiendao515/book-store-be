package box.bookstorebe.dto.ghtk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class GhtkOrderDetailDto {
    private boolean success;
    private String message;
    private OrderDetail order;
    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public class OrderDetail {
        @JsonProperty("label_id")
        private String labelId;

        @JsonProperty("partner_id")
        private String partnerId;

        @JsonProperty("order_id")
        private String orderId;

        private int status;

        @JsonProperty("status_text")
        private String statusText;

        private String message;

        @JsonProperty("customer_fullname")
        private String customerFullname;

        @JsonProperty("customer_tel")
        private String customerTel;

        private String address;

        @JsonProperty("storage_day")
        private int storageDay;

        @JsonProperty("ship_money")
        private int shipMoney;

        private int insurance;

        private int value;

        private int weight;

        @JsonProperty("pick_money")
        private int pickMoney;

        @JsonProperty("is_freeship")
        private int isFreeship;

    }
}
