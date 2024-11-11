package box.bookstorebe.service.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.payment.PaymentDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.repository.book.BookRepository;
import box.bookstorebe.repository.payment.PaymentRepository;
import box.bookstorebe.service.book.BookService;
import box.bookstorebe.service.order.OrderService;
import box.bookstorebe.service.order.PaymentService;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationResults;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import static org.springframework.data.mongodb.core.aggregation.Aggregation.*;


@Service
@Slf4j
@AllArgsConstructor
public class MailService {
    private final MongoTemplate mongoTemplate;
    private final BookRepository bookRepository;
    private final JavaMailSender javaMailSender;

    public SimpleMailMessage constructResetTokenEmail(String baseUrl, String token, AccountDocument user, String message) {
        final String url = baseUrl + "/reset-password?token=" + token;
        final SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(user.getEmail());
        email.setSubject("Reset Password");
        email.setText(message + " \r\n" + url);
        return email;
    }

    public SimpleMailMessage constructEmailMessage(OnRegistrationCompleteEvent event, AccountDocument user, String token) {
        String recipientAddress = user.getEmail();
        String subject = "Complete Registration";
        String confirmationUrl = event.getAppUrl() + "/confirm-registration?token=" + token;
        String message = "To confirm your account, please click the link below:\n" + confirmationUrl;
        SimpleMailMessage email = new SimpleMailMessage();
        email.setTo(recipientAddress);
        email.setSubject(subject);
        email.setText(message);
        return email;
    }
    public void sendEmailOrderDetail(String to, OrderDocument orderDocument) throws MessagingException, BizException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[Mở hộp] Đơn sách chi tiết ngày " +orderDocument.getCreatedAt().getDayOfMonth()+"/"+
                orderDocument.getCreatedAt().getMonthValue()+ "/"+orderDocument.getCreatedAt().getYear());
        helper.setText(formatOrderDetail(orderDocument), true); // Set to true to send HTML content
        javaMailSender.send(mimeMessage);
    }
    public String formatOrderDetail(OrderDocument order) throws BizException {
        StringBuilder builder = new StringBuilder();
        ZonedDateTime dateCreated = order.getCreatedAt();
        PaymentDocument paymentDocument= mongoTemplate.findOne(new Query(Criteria.where("order._id").is(order.getId())), PaymentDocument.class);
        builder.append("<html>")
                .append("<head>")
                .append("<style>")
                .append("table {width: 100%; border-collapse: collapse;}")
                .append("th, td {border: 1px solid black; padding: 8px; text-align: left;}")
                .append("th {background-color: #f2f2f2;}")
                .append("</style>")
                .append("</head>")
                .append("<body>")
                .append("<h2>[Mở Hộp] Đơn sách chi tiết ngày ")
                .append(dateCreated.getDayOfMonth()).append("/")
                .append(dateCreated.getMonthValue()).append("/")
                .append(dateCreated.getYear()).append("</h2>")
//                .append("<p>Thân chào ").append(order.getCustomerName()).append(",</p>")
                .append("<p>Cảm ơn bạn đã mua hàng tại mở hộp. Dưới đây là thông tin hóa đơn chi tiết của bạn:</p>")
                .append("<h3>Thông tin khách hàng</h3>")
//                .append("<p>Tên khách hàng: ").append(order.getCustomerName()).append("</p>")
//                .append("<p>Số điện thoại: ").append(order.getCustomerPhone()).append("</p>")
//                .append("<p>Email: ").append(order.getEmail()).append("</p>")
                .append("<p>Địa chỉ: ").append(order.getAddress()).append("</p>")
                .append("<h3>Thông tin đơn sách</h3>")
                .append("<p>Mã đơn hàng: ").append(order.getOrderCode()).append("</p>")
                .append("<p>Ngày đặt đơn: ").append(dateCreated.getDayOfMonth()).append("/")
                .append(dateCreated.getMonthValue()).append("/")
                .append(dateCreated.getYear()).append("</p>")
                .append("<table>")
                .append("<tr><th>Tên sách</th><th>Tình trạng</th><th>Số lượng</th><th>Giá bán (đồng)</th></tr>");

        BigDecimal totalPay = new BigDecimal(0);
        Map<String, Integer> bookCountMap = new HashMap<>();
//        for (BookInventory bookRealityDocument : order.getItems()) {
//            Optional<BookDocument> bookDocument = bookRepository.findById(bookRealityDocument.getBookId());
//            String key = bookRealityDocument.getBookId() + "-" + bookRealityDocument.getType() + "-" + bookRealityDocument.getPrice() + "-" + bookDocument.get().getName();
//            bookCountMap.put(key, bookCountMap.getOrDefault(key, 0) + 1);
//            totalPay.add(new BigDecimal(bookRealityDocument.getPrice()));
//        }

        for (Map.Entry<String, Integer> entry : bookCountMap.entrySet()) {
            String[] parts = entry.getKey().split("-");
            String name = parts[3];
            String status = parts[1];
            int count = entry.getValue();
            String price = parts[2];

            builder.append("<tr>")
                    .append("<td>").append(name).append("</td>")
                    .append("<td>").append(status).append("</td>")
                    .append("<td>").append(count).append("</td>")
                    .append("<td>").append(price).append("</td>")
                    .append("</tr>");
        }

        builder.append("</table>")
                .append("<p>Phí giao hàng:")
                .append(totalPay.compareTo(Const.AMOUNT_CAN_FREESHIP) < 0 ? 25000 : 0).append("</p>")
                .append("<p>Tổng tiền: ").append(totalPay.compareTo(Const.AMOUNT_CAN_FREESHIP) <0 ?
                        totalPay.add(Const.SHIPPING_FEE) : totalPay).append("</p>")
                .append("<h3>Phương thức thanh toán</h3>");

        if (order.isPaymentType()) {
            builder.append("<p>Chuyển khoản qua ví điện tử VNPAY</p>");
        } else {
            builder.append("<p>COD (thanh toán khi nhận hàng)</p>");
        }

        builder.append("<h3>Tình trạng đơn hàng</h3>")
                // Uncomment and implement the following lines if payment success is to be checked
                 .append("<p>")
                 .append(paymentDocument!=null ? "Đơn hàng đã được thanh toán thành công" : "Đơn hàng chưa được thanh toán")
                 .append("</p>")
                .append("<p>Mong rằng bạn đã có trải nghiệm sản phẩm dễ chịu. Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi qua email này hoặc gửi đơn khiếu nại như trên web: <<link web>>. Chúng tôi sẽ liên hệ với bạn trong vòng 24h kể từ khi nhận được thông tin.</p>")
                .append("<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>")
                .append("<p>Trân trọng,</p>")
                .append("<p>mở hộp</p>")
                .append("</body>")
                .append("</html>");

        return builder.toString();
    }


}
