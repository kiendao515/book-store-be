package box.bookstorebe.model.auth;

import box.bookstorebe.common.ZonedDateTimeDeserializer;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateUserInfoModel {
    @JsonDeserialize(using= ZonedDateTimeDeserializer.class)
    private ZonedDateTime dateOfBirth;
    private Integer point;
    private String fullName;
    private String phoneNumber;
}