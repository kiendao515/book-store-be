package box.bookstorebe.dto.bookstore;

import box.bookstorebe.document.book.BookDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class StoreRevenueDto {
    private String id;
    private BookDocument book;
    private Integer inventory;
    private Integer sold;
    private Integer settle; // số quyển đã chốt
    private Integer notSettle; // số quyển chưa chốt
    private BigDecimal settleAmount;// số tiền đã chốt
    private BigDecimal notSettleAmount;
    private Float commissionPercentage;
}
