package box.bookstorebe.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.ZonedDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class NotificationLog {
    private String source;
    private String sender;
    private String content;
    private boolean status;
    private ZonedDateTime createdAt;
    private ZonedDateTime updatedAt;
}
