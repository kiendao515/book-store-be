package box.bookstorebe.dto.book;

import box.bookstorebe.document.book.CategoryDocument;
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
public class BookDto {
    private String id;
    private String name;
    private Long numberOfPage;
    private String description;
    private Integer publishYear;
    private String isbn;
    private String authorName;
    private String publisher;
    private String coverImage;
    private String backImage;
    private List<String> demoImages;
    private String demoUrl;
    private List<String> tags;
    private CategoryDocument category;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
