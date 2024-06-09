package box.bookstorebe.eventlistener.event;

import box.bookstorebe.common.Const;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@NoArgsConstructor
public class BaseEvent {
    public Const.EventType eventType;
}
