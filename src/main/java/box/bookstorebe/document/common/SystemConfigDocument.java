package box.bookstorebe.document.common;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Document("system_configs")
public class SystemConfigDocument {
    @Id
    private String id;

    @Field("key")
    private String key;

    @Field("value")
    private String value;

    @Field("data_type")
    private String dataType;
}
