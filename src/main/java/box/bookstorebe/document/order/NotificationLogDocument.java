package box.bookstorebe.document.order;

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
@Document("notification_logs")
public class NotificationLogDocument {
    @Id
    private String id;
    @Field(name= "source")
    private String source;
    @Field(name= "sender")
    private String sender;
    @Field(name = "content")
    private String content;
    @Field(name = "status")
    private boolean status;
    @Field(name = "createdAt")
    private ZonedDateTime createdAt;
    @Field(name = "updatedAt")
    private ZonedDateTime updatedAt;
}
