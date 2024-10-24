package box.bookstorebe.model.bookstore;

import box.bookstorebe.document.bookstore.BookStoreDocument;
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
public class CreateBookStoreModel {
    private String name;
    private String imageId;
    private String address;
    private String phoneNumber;
//    private String description;
}
