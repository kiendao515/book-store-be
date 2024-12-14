package box.bookstorebe.model.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OfflineOrderDbModel {
    private String id;
    private String userId;
    private String bookStoreId;
    private Double totalBookPrice;
    private Double bookDiscount;
    private Double billDiscount;
    private Double totalPrice;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;

}
