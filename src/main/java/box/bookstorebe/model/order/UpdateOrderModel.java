package box.bookstorebe.model.order;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateOrderModel {
    private String address;
    private String customerName;
    private String customerPhone;
    private String email;
    private String status;
    private String note;
    private String shippingCode;
}
