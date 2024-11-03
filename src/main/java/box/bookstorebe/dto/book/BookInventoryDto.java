package box.bookstorebe.dto.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookType;
import box.bookstorebe.document.common.ImageDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookInventoryDto {
    private String id;
    private String bookId;
    private BigDecimal price;
    private String location; // vị trí sách ở Hộp
    private Integer quantity;
    private BookType type; // NEW, LIKE NEW, OLD
    private String storeId;
    private String coverImage;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
