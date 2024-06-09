package box.bookstorebe.dto.common;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class SystemConfigDto {
    private String id;

    @NotBlank
    private String key;

    @NotBlank
    private String value;

    @NotBlank
    private String dataType;
}
