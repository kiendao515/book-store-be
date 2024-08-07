package box.bookstorebe.model.booksearchrequest.booksearchrequest;

import box.bookstorebe.common.Const;
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
    private List<BookRequest> bookRequests;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
    public static class BookRequest {
        private String id;
        private String bookName;
        private String authorName;
        private Const.BookSearchRequestStatus status;
    }
}
