package box.bookstorebe.dto.customer;

import box.bookstorebe.document.account.CustomerDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Field;

import java.util.List;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CustomerInfoDto {
    private String email;
    private String id;
    private String accountId;
    private String name;
    private String phoneNumber;
    private List<String> address;
    private Integer isEnabled;
}
