package box.bookstorebe.document.order;
import box.bookstorebe.dto.common.AddressDto;
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

    @Field(name= "account_id")
    private String accountId;

    @Field(name = "receiver_name")
    private String receiverName;

    @Field(name = "receiver_phone")
    private String receiverPhone;

    @Field(name = "province")
    private AddressDto.AddressDetail province;

    @Field(name = "district")
    private AddressDto.AddressDetail district;

    @Field(name = "ward")
    private AddressDto.AddressDetail ward;

    @Field(name = "street")
    private String street;

//    @Field(name = "address")
//    private String address;

    @Field(name = "status")
    private String status;// created -> cancel -> confirm -> shipping -> done

    @Field(name = "payment_type")
    private boolean paymentType; // 0 for cod, 1 for pay by wallet

    @Field(name = "discount_point")
    private BigDecimal discountPoint;

    @Field(name = "shipping_fee")
    private BigDecimal shippingFee;

    @Field(name = "total_amount")
    private BigDecimal totalAmount;

    @Field(name = "transaction_id")
    private String transactionId; // dùng cho vnpay

//    @Field(name = "payment_time")
//    private ZonedDateTime paymentTime;

    @Field(name = "order_code")
    @Indexed
    private String orderCode;

    @Field(name = "shipping_code")
    private String shippingCode;

    @Field(name = "shipping_company")
    private String shippingCompany;

    @Field(name = "note")
    private String note;

    @Field(name = "weight")
    private Float weight;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;

}
