package box.bookstorebe.service.notification;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.book.BookRealityDocument;
import box.bookstorebe.document.order.NotificationLogDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.model.order.NotificationLog;
import box.bookstorebe.repository.order.NotificationLogRepository;
import box.bookstorebe.repository.order.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;

@AllArgsConstructor
@Service
@Slf4j
public class BankBalanceNotificationService {
    private final NotificationLogRepository notificationLogRepository;
    private final OrderRepository orderRepository;
    public static String parseTransferContent(String message) {
        String content = "";
        String regex = "Nội dung:.*?\\s([A-Z\\s]+.*?Chuyen tien)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            content = matcher.group(1);
        }

        return content.trim();
    }
    public static String parseTransferAmount(String message) {
        String amount = "";
        String regex = "Giao dịch:\\s*(-?[\\d,]+VND)";
        java.util.regex.Pattern pattern = java.util.regex.Pattern.compile(regex);
        java.util.regex.Matcher matcher = pattern.matcher(message);

        if (matcher.find()) {
            amount = matcher.group(1);
        }

        return amount.trim();
    }
    public void createNotificationLog(NotificationLog notificationLog){
        if("".equals(notificationLog.getContent()) && "".equals(notificationLog.getSender()) &&
                "".equals(notificationLog.getSource())) return;
        NotificationLogDocument notificationLogDocument = new NotificationLogDocument();
        notificationLogDocument.setCreatedAt(ZonedDateTime.now());
        notificationLogDocument.setStatus(false);
        notificationLogDocument.setContent(notificationLog.getContent());
        notificationLogDocument.setSource(notificationLog.getSource());
        notificationLogDocument.setSender(notificationLog.getSender());
        notificationLogRepository.save(notificationLogDocument);
        String transferContent = parseTransferContent(notificationLog.getContent());
        log.info("noi dung ck {}",transferContent);
        OrderDocument orderDocument = orderRepository.findByOrderId(transferContent);
        BigDecimal amount =new BigDecimal(0);
        if(orderDocument!=null){
            for (BookRealityDocument bookRealityDocument :orderDocument.getItems()) {
                amount.add(new BigDecimal(bookRealityDocument.getPrice()));
            }
            String amountTransaction = parseTransferAmount(notificationLog.getContent());
            log.info("tong so tien ck {}",transferContent);
            if(new BigDecimal(amountTransaction).compareTo(amount)==0){
                orderDocument.setStatus(Const.OrderStatus.READY_TO_PACKAGE);
            }
        }
    }

}
