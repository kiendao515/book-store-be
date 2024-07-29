package box.bookstorebe.dto.user;

import box.bookstorebe.document.user.Role;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UserDto {
    private String id;
    @Field("email")
    private String email;
    @Field("full_name")
    private String fullName;
    @Field("role")
    private Role role;
}
