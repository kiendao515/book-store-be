package box.bookstorebe.eventlistener.listener;

import box.bookstorebe.eventlistener.event.BaseEvent;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class BaseEventListener {
    @EventListener
    void handleBaseEvent(BaseEvent baseEvent) {
    }
}
