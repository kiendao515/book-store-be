package box.bookstorebe.document.account;

import box.bookstorebe.dto.common.AddressDto;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("shipping_addresses")
public class ShippingAddressDocument {
    @Id
    private String id;

    @Field(name = "account_id")
    private String userId;

    @Field(name = "full_name")
    private String fullName;

    @Field(name = "phone_number")
    private String phoneNumber;

    @Field(name = "province")
    private AddressDto.AddressDetail province;

    @Field(name = "district")
    private AddressDto.AddressDetail district;

    @Field(name = "ward")
    private AddressDto.AddressDetail ward;

    @Field(name = "street")
    private String street;

    @Field(name = "is_default")
    private boolean isDefault;

}
