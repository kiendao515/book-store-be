package box.bookstorebe.model.bookstore;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class CreateBookStoreModel {
    private String name;
    private String thumbnail;
    private String address;
    private String phoneNumber;
    private String description;
    private Float commissionPercentage;
}
