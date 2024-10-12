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
    @Field(name = "payment_time")
    private String paymentTime;

    @Field(name = "total_price")
    private BigDecimal totalPrice;

    @Field(name = "transactionId")
    private String transactionId;
}
