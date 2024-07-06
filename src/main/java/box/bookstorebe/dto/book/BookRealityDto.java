package box.bookstorebe.dto.book;

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
public class BookRealityDto {
    private String id;
    private Double price;
    private String status;
    private String type;
    private List<String> imageLinks;
    private BookDto bookDetail;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
