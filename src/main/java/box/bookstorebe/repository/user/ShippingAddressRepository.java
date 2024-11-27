package box.bookstorebe.repository.user;

import box.bookstorebe.document.account.ShippingAddressDocument;
import box.bookstorebe.repository.user.ex.ShippingAddressExRepository;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface ShippingAddressRepository extends MongoRepository<ShippingAddressDocument, String>, ShippingAddressExRepository {
    List<ShippingAddressDocument> findAllByUserIdInAndIsDefault(List<String> userIds, Boolean isDefault);
}
