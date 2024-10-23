package box.bookstorebe.document.order;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("orders")
public class OrderDocument {
    @Id
    private String id;

    @Field(name= "user_id")
    private String userId;

    @Field(name = "address")
    private String address;

    @Field(name = "customer_name")
    private String customerName;

    @Field(name = "customer_phone")
    private String customerPhone;

    @Field(name = "email")
    private String email;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;

    @Field(name = "status")
    private String status;// created -> cancel -> confirm -> shipping -> done

    @Field(name = "payment_type")
    private boolean paymentType; // 0 for cod, 1 for pay by wallet

    @Field(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Field(name = "order_code")
    @Indexed
    private String orderCode;
    @Field(name = "shipping_code")
    private String shippingCode;
    @Field(name = "shipping_company")
    private String shippingCompany;
    @Field(name = "note")
    private String note;

}
