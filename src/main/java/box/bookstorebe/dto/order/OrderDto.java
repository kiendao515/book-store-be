package box.bookstorebe.dto.order;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.book.BookDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
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
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private boolean isPaid;
    private boolean paymentType;
    private String note;
    private String shippingCode;
    private String shippingCompany;
    private String orderCode;
    private List<OrderItemDto> orderItems;
    private BigDecimal shippingFee;
    private BigDecimal totalAmount;
    private AccountDto account;
    private String transactionId;
    private BigDecimal discountPoint;
    private String relatedOrderId;
}
