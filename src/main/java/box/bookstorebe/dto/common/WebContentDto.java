package box.bookstorebe.dto.common;

import box.bookstorebe.document.common.ImageDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class WebContentDto {
    private String id;
    private String key;
    private String pageName;
    private String title;
    private String property; // IMAGE, TEXT
    private String value;
    private ImageDocument image;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
