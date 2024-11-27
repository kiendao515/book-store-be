package box.bookstorebe.dto.customer;

import box.bookstorebe.document.account.ShippingAddressDocument;
import box.bookstorebe.dto.common.AddressDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class ShippingAddressDto {
    private String id;
    @Field("full_name")
    private String fullName;
    @Field("phone_number")
    private String phoneNumber;
    @Field("province")
    private AddressDto.AddressDetail province;
    @Field("district")
    private AddressDto.AddressDetail district;
    @Field("ward")
    private AddressDto.AddressDetail ward;
    @Field("street")
    private String street;
    @Field("is_default")
    private boolean isDefault;
}
