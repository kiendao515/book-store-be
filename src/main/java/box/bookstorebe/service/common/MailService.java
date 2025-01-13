package box.bookstorebe.service.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.book.BookDocument;
import box.bookstorebe.document.book.BookInventory;
import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.order.OrderItemDocument;
import box.bookstorebe.document.payment.PaymentDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.order.OrderDto;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.OrderItem;
import box.bookstorebe.repository.book.BookInventoryRepository;
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
    private final BookInventoryRepository bookInventoryRepository;

    public SimpleMailMessage constructResetTokenEmail(String baseUrl, String token, AccountDocument user, String message) {
        final String url = baseUrl + "/reset-password?token=" + token + "&is_reset_pwd=1";
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
    public void sendMailInStock(String to, String bookDocument) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[Mở hộp] Sách bạn yêu thích hiện mới có thêm hàng");
        helper.setText("Truy cap ngay de mua them sách mới:"+ "https://hopsach.sytes.net/"+bookDocument, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendMailStoreInfo(String to, String pass, StoreDocument storeDocument) throws MessagingException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[Mở hộp] Thông tin tài khoản nhà bán của bạn");
        String content = formatStoreDetail(pass, storeDocument);
        helper.setText(content, true);
        javaMailSender.send(mimeMessage);
    }

    public void sendEmailOrderDetail(String to, OrderDocument orderDocument, List<OrderItemDocument> orderItemDocuments) throws MessagingException, BizException {
        MimeMessage mimeMessage = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true, "UTF-8");
        helper.setTo(to);
        helper.setSubject("[Mở hộp] Đơn sách chi tiết ngày " + orderDocument.getCreatedAt().getDayOfMonth() + "/" +
                orderDocument.getCreatedAt().getMonthValue() + "/" + orderDocument.getCreatedAt().getYear());
        helper.setText(formatOrderDetail(orderDocument, orderItemDocuments), true); // Set to true to send HTML content
        javaMailSender.send(mimeMessage);
    }

    public String formatOrderDetail(OrderDocument order, List<OrderItemDocument> orderItemDocuments) throws BizException {
        StringBuilder builder = new StringBuilder();
        ZonedDateTime dateCreated = order.getCreatedAt();

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
                .append("<p>Thân chào ").append(order.getReceiverName()).append(",</p>")
                .append("<p>Cảm ơn bạn đã mua hàng tại Mở Hộp. Dưới đây là thông tin hóa đơn chi tiết của bạn:</p>")
                .append("<h3>Thông tin khách hàng</h3>")
                .append("<p>Tên khách hàng: ").append(order.getReceiverName()).append("</p>")
                .append("<p>Số điện thoại: ").append(order.getReceiverPhone()).append("</p>")
                .append("<p>Địa chỉ: ").append(order.getStreet()).append(", ")
                .append(order.getWard().getFullName()).append(", ")
                .append(order.getDistrict().getFullName()).append(", ")
                .append(order.getProvince().getFullName()).append("</p>")
                .append("<h3>Thông tin đơn sách</h3>")
                .append("<p>Mã đơn hàng: ").append(order.getOrderCode()).append("</p>")
                .append("<p>Ngày đặt đơn: ").append(dateCreated.getDayOfMonth()).append("/")
                .append(dateCreated.getMonthValue()).append("/")
                .append(dateCreated.getYear()).append("</p>")
                .append("<table>")
                .append("<tr><th>Tên sách</th><th>Tình trạng</th><th>Số lượng</th><th>Giá bán (đồng)</th></tr>");

        BigDecimal totalPay = BigDecimal.ZERO;
        Map<String, Integer> bookCountMap = new HashMap<>();

        for (OrderItemDocument item : orderItemDocuments) {
            BookInventory bookInventory = bookInventoryRepository.findById(item.getBookInventoryId())
                    .orElseThrow(() -> new BizException("Không tìm thấy thông tin sách trong kho."));

            BookDocument bookInfo = bookRepository.findById(bookInventory.getBookId())
                    .orElseThrow(() -> new BizException("Không tìm thấy thông tin sách trong hệ thống."));

            String formattedPrice = bookInventory.getPrice().stripTrailingZeros().toPlainString();
            String key = bookInfo.getName() + ";" + bookInventory.getType() + ";" + formattedPrice;
            bookCountMap.put(key, bookCountMap.getOrDefault(key, 0) + item.getQuantity());

            totalPay = totalPay.add(bookInventory.getPrice().multiply(BigDecimal.valueOf(item.getQuantity())));
        }

        for (Map.Entry<String, Integer> entry : bookCountMap.entrySet()) {
            String[] parts = entry.getKey().split(";");
            String name = parts[0];
            String condition = parts[1];
            BigDecimal price = new BigDecimal(parts[2]);
            int count = entry.getValue();

            builder.append("<tr>")
                    .append("<td>").append(name).append("</td>")
                    .append("<td>").append(condition).append("</td>")
                    .append("<td>").append(count).append("</td>")
                    .append("<td>").append(price.multiply(BigDecimal.valueOf(count))).append("</td>")
                    .append("</tr>");
        }

        builder.append("</table>")
                .append("<p>Phí giao hàng: ")
                .append(order.getShippingFee()).append("</p>")
                .append("<p>Tổng tiền: ")
                .append(totalPay.add(totalPay.compareTo(Const.AMOUNT_CAN_FREESHIP) < 0 ? Const.SHIPPING_FEE : BigDecimal.ZERO))
                .append("</p>")
                .append("<h3>Phương thức thanh toán</h3>");

        if (order.isPaymentType()) {
            builder.append("<p>Chuyển khoản qua ví điện tử VNPAY</p>");
        } else {
            builder.append("<p>COD (thanh toán khi nhận hàng)</p>");
        }

        builder.append("<h3>Tình trạng đơn hàng</h3>")
                .append("<p>")
                .append(order.getTransactionId() != null ? "Đơn hàng đã được thanh toán thành công" : "Đơn hàng chưa được thanh toán")
                .append("</p>")
                .append("<p>Mong rằng bạn đã có trải nghiệm sản phẩm dễ chịu. Nếu bạn có bất kỳ thắc mắc nào, vui lòng liên hệ với chúng tôi qua email hoặc gửi đơn khiếu nại qua website: <<link web>>. Chúng tôi sẽ liên hệ với bạn trong vòng 24h kể từ khi nhận được thông tin.</p>")
                .append("<p>Cảm ơn bạn đã sử dụng dịch vụ của chúng tôi!</p>")
                .append("<p>Trân trọng,</p>")
                .append("<p>Mở Hộp</p>")
                .append("</body>")
                .append("</html>");

        return builder.toString();
    }

    private String formatStoreDetail(String password, StoreDocument storeDocument) {
        return """
                <html>
                    <head>
                        <style>
                            .store-thumbnail {
                                display: block;
                                margin: 20px auto;
                                width: 200px;
                                height: auto;
                                border-radius: 10px;
                                box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
                            }
                            .store-info {
                                font-family: Arial, sans-serif;
                                line-height: 1.6;
                                color: #333;
                                max-width: 600px;
                                margin: auto;
                                padding: 20px;
                                background-color: #f9f9f9;
                                border-radius: 10px;
                                box-shadow: 0 4px 8px rgba(0, 0, 0, 0.1);
                            }
                            .store-info h2 {
                                text-align: center;
                                color: #2c3e50;
                            }
                            .store-info p {
                                margin: 10px 0;
                            }
                        </style>
                    </head>
                    <body>
                        <div class="store-info">
                            <h2>Thông tin tài khoản nhà bán của bạn</h2>
                            <p><strong>Mật khẩu:</strong> %s</p>
                            <p><strong>Tên nhà bán:</strong> %s</p>
                            <p><strong>Mô tả:</strong> %s</p>
                            <p><strong>Số điện thoại:</strong> %s</p>
                            <p><strong>Địa chỉ:</strong> %s</p>
                            <p><strong>Tỷ lệ hoa hồng:</strong> %.2f%%</p>
                            <img src="%s" alt="Store Thumbnail" class="store-thumbnail">
                        </div>
                    </body>
                </html>
                """.formatted(
                password,
                storeDocument.getName(),
                storeDocument.getDescription(),
                storeDocument.getPhoneNumber(),
                storeDocument.getAddress(),
                storeDocument.getCommissionPercentage(),
                storeDocument.getThumbnail()
        );
    }


}
