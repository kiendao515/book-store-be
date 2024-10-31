package box.bookstorebe.document.payment;

import box.bookstorebe.document.order.OrderDocument;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("payments")
@Builder
public class PaymentDocument {
    @Id
    private String id;
    @Field(name = "order")
    private OrderDocument order;
    @Field(name = "amount")
    private BigDecimal totalPrice;
    @Field(name = "transactionId")
    private String transactionId;
    @Field(name = "createdAt")
    private ZonedDateTime createdAt;
    @Field(name = "updatedAt")
    private ZonedDateTime updatedAt;
}
