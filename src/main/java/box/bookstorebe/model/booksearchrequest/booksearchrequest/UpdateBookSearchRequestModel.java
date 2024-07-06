package box.bookstorebe.model.booksearchrequest.booksearchrequest;

import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.booksearchrequest.BookSearchRequestDocument;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class UpdateBookSearchRequestModel {
    private String userId;
    private String fullName;
    private String email;
    private String phoneNumber;
    private List<BookSearchRequestDocument.BookRequest> bookRequests;
}
