package box.bookstorebe.document.book;

import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;
import java.util.List;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Document("books")
public class BookDocument extends BookCommon {
    @Id
    private String id;

    @Field("name")
    private String name;

    @Field("collection_ids")
    private List<String> collectionIds;

    @Field("category_ids")
    private List<String> categoryIds;

    @Field("store_id")
    private String storeId;
}
