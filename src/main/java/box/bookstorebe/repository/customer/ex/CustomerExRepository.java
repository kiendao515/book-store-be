package box.bookstorebe.repository.customer.ex;

import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.customer.CustomerInfoDto;
import org.springframework.data.domain.Page;

public interface CustomerExRepository {
    Page<CustomerDocument> getCustomers(String role,String name, String phone,String address, Integer page, Integer size);
}
