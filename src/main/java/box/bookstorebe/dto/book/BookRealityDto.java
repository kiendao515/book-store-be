package box.bookstorebe.dto.book;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.common.ImageDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class BookRealityDto {
    private String id;
    private Double price;
    private Const.BookRealityStatus status;
    private Const.BookRealityType type;
    private BookDto book;
    private ImageDocument coverImage;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
