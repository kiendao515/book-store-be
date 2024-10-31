package box.bookstorebe.eventlistener.event;


import box.bookstorebe.document.account.AccountDocument;
import lombok.Getter;
import lombok.Setter;
import org.springframework.context.ApplicationEvent;


@Getter
@Setter
public class OnRegistrationCompleteEvent extends ApplicationEvent {
    private String appUrl;
    private AccountDocument user;

    public OnRegistrationCompleteEvent(Object source, AccountDocument user, String appUrl) {
        super(source);
        this.user = user;
        this.appUrl = appUrl;
    }

}
