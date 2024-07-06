package box.bookstorebe.dto.booksearchrequest;

import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
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
public class BookSearchRequestDto {
    private String id;
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<BookSearchRequestDocument.BookRequest> bookRequests;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
