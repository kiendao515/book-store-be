package box.bookstorebe.repository.user.ex;

import box.bookstorebe.dto.customer.ShippingAddressDto;
import org.springframework.data.domain.Page;

public interface ShippingAddressExRepository {
    Page<ShippingAddressDto> getShippingAddresses(String userId, Integer page, Integer size);
}
