package box.bookstorebe.dto.account;

import box.bookstorebe.document.account.Role;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountDto {
    private String id;
    @Field("email")
    private String email;
    @Field("role")
    private Role role;
    @Field("enabled")
    private Integer enabled;
    @Field("name")
    private String name;
    @Field("phone")
    private String phone;
    @Field("address")
    private String address;
    @Field("avatar")
    private String avatar;
    @Field("description")
    private String description;
    @Field("created_at")
    private ZonedDateTime createdAt;
    @Field("orders_completed")
    private Long ordersCompleted;
    @Field("total_amount")
    private BigDecimal totalAmount;
    @Field("commission_percentage")
    private Float commissionPercentage;
}
