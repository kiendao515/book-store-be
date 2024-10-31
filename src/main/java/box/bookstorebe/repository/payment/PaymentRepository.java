package box.bookstorebe.repository.payment;

import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.document.payment.PaymentDocument;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PaymentRepository extends MongoRepository<PaymentDocument, String>{
}
