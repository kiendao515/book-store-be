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
public class OfflineBookDetailDto {
    private String bookInventoryId;
    private String barcode;
    private String bookName;
    private String author;
    private String type;
    private BigDecimal price;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
