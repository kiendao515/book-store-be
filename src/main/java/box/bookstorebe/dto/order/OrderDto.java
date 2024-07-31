package box.bookstorebe.dto.order;

import box.bookstorebe.dto.book.BookDto;
import box.bookstorebe.dto.book.BookRealityDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderDto {
    private String id;
    private String address;
    private String customerName;
    private String customerPhone;
    private String email;
    private String status;
    private List<BookRealityDto> books;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private boolean isPaid;
    private boolean paymentType;
}
