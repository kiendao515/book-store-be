package box.bookstorebe.dto.account;

import box.bookstorebe.document.account.Role;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class AccountDto {
    private String id;
    @Field("email")
    private String email;
    @Field("full_name")
    private String fullName;
    @Field("role")
    private Role role;
}
