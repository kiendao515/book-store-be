package box.bookstorebe.eventlistener.event;


import box.bookstorebe.document.user.UserDocument;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private UserDocument user;

    public OnRegistrationCompleteEvent(Object source, UserDocument user, String appUrl) {
        super(source);
        this.user = user;
        this.appUrl = appUrl;
    }

}
