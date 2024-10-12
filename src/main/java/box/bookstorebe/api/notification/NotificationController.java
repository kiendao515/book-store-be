package box.bookstorebe.api.notification;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.model.order.NotificationLog;
import box.bookstorebe.service.notification.BankBalanceNotificationService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
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
