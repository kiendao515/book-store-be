package box.bookstorebe.dto.book;

import box.bookstorebe.document.book.CategoryDocument;
import box.bookstorebe.document.bookstore.BookStoreDocument;
import box.bookstorebe.document.common.CommonEntity;
import box.bookstorebe.document.common.ImageDocument;
import box.bookstorebe.document.common.PersonDocument;
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
    private ImageDocument coverImage;
    private ImageDocument detailImage;
    private List<ImageDocument> demoImages;
    private String demoUrl;
    private List<String> tags;
    private CategoryDocument category;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
