package box.bookstorebe.dto.bookstore;

import box.bookstorebe.dto.book.BookInventoryDto;
import box.bookstorebe.dto.order.OrderItemDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class DetailBookRevenue {
    private List<BookInventoryDto> inventory;
    private List<OrderItemDto> orderItems;
    private BigDecimal totalAmountInventory;
    private BigDecimal totalAmountSold;

}
