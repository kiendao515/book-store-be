package box.bookstorebe.document.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import java.time.ZonedDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("web_contents")
public class WebContentDocument {
    @Id
    private String id;

    @Field(name = "key")
    private String key;

    @Field(name = "title")
    private String title;

    @Field(name = "property")
    private String property;

    @Field(name = "value")
    private String value;

    @Field(name = "created_at")
    private ZonedDateTime createdAt;

    @Field(name = "updated_at")
    private ZonedDateTime updatedAt;
}

