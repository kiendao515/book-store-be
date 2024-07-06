package box.bookstorebe.dto.bookstore;

import box.bookstorebe.document.bookstore.BookStoreDocument;
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
public class BookStoreDto {
    private String id;
    private String name;
    private List<BookStoreDocument.Address> address;
    private List<BookStoreDocument.OtherInformation> otherInformation;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
