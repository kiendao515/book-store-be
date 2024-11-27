package box.bookstorebe.model.user;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShippingAddressModel {
    private String fullName;
    private String phoneNumber;
    private String provinceCode;
    private String districtCode;
    private String wardCode;
    private String street;
    private boolean isDefault;
}
