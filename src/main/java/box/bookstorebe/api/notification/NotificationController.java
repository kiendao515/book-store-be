package box.bookstorebe.api.notification;

import box.bookstorebe.exception.BizException;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/notifications")
@RequiredArgsConstructor
public class NotificationController {
    private final BankBalanceNotificationService bankBalanceNotificationService;
    @PostMapping
    public void checkBankBalance(@RequestBody @Valid NotificationLog notificationLog) throws BizException{
        bankBalanceNotificationService.createNotificationLog(notificationLog);
    }
}
