package box.bookstorebe.model.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateOrderModel {
    private String customerName;
    private String customerPhone;
    private String email;
    private List<OrderItem> orderItems;
    private boolean paymentMethod;
    private String note;
    private String provinceCode;
    private String districtCode;
    private String wardCode;
    private String street;
}
