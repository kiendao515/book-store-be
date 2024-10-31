package box.bookstorebe.repository.customer;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.repository.customer.ex.CustomerExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface CustomerRepository extends MongoRepository<CustomerDocument, String>, CustomerExRepository {
    List<CustomerDocument> findAllByAccountIdIn(List<String> accountIds);
    CustomerDocument findByAccountId(String accountId);
}
