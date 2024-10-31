package box.bookstorebe.dto.common;

import box.bookstorebe.document.common.PersonDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class PersonDto {
    private String id;
    private String name;
    private String type;
    private List<PersonDocument.Description> descriptions;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
    private boolean nationality;
}
