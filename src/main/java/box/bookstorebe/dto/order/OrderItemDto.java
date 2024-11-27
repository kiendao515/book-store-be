package box.bookstorebe.dto.order;

import box.bookstorebe.document.book.BookType;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Builder
@Getter
@Setter
public class OrderItemDto {
    private String bookName;
    private int quantity;
    private BigDecimal price;
    private BookType type;
}
