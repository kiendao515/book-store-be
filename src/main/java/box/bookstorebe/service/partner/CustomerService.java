package box.bookstorebe.service.partner;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.customer.CustomerInfoDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.account.AccountMapper;
import box.bookstorebe.model.user.CustomerModel;
import box.bookstorebe.model.user.UpdateCustomerModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.customer.CustomerRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.account.AccountService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class CustomerService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final AccountService accountService;

    public Page<CustomerInfoDto> getCustomerInfo(String role, String name, String phone, String address, Integer page, Integer size) throws BizException {
        Page<CustomerDocument> customerDocuments = customerRepository.getCustomers(role, name, phone, address, page, size);
        return customerDocuments.map(customerDocument -> {
            AccountDocument acc = null;
            try {
                acc = accountRepository.findById(customerDocument.getAccountId()).orElseThrow(() -> new BizException("invalid acc id"));
            } catch (BizException e) {
                throw new RuntimeException(e);
            }
            CustomerInfoDto customerInfoDto = new CustomerInfoDto();
            customerInfoDto.setId(customerDocument.getId());
            customerInfoDto.setName(customerDocument.getName());
            customerInfoDto.setEmail(acc.getEmail());
            customerInfoDto.setAccountId(acc.getId());
            customerInfoDto.setPhoneNumber(customerDocument.getPhoneNumber());
            customerInfoDto.setAddress(customerDocument.getAddress());
            customerInfoDto.setIsEnabled(acc.getEnabled());
            return customerInfoDto;
        });
    }

    public CustomerInfoDto getCustomerInfoDetail(String id) throws BizException {
        CustomerDocument customerDocument = customerRepository.findById(id).orElseThrow(() -> new BizException("invalid customer id"));
        AccountDocument accountDocument = accountRepository.findById(customerDocument.getAccountId()).orElseThrow(() -> new BizException("invalid account id"));
        CustomerInfoDto customerInfoDto = new CustomerInfoDto();
        customerInfoDto.setId(customerDocument.getId());
        customerInfoDto.setName(customerDocument.getName());
        customerInfoDto.setEmail(accountDocument.getEmail());
        customerInfoDto.setAccountId(accountDocument.getId());
        customerInfoDto.setPhoneNumber(customerDocument.getPhoneNumber());
        customerInfoDto.setAddress(customerDocument.getAddress());
        return customerInfoDto;
    }

    public void createCustomerInfoAndAccount(CustomerModel customerModel) throws BizException {
        CustomerDocument customerDocument = new CustomerDocument();
        customerDocument.setAddress(customerModel.getAddress());
        customerDocument.setPhoneNumber(customerModel.getPhone());
        customerDocument.setName(customerModel.getName());
        AccountDocument accountDocument = accountService.createAccount(new UserModel(customerModel.getEmail(), customerModel.getPassword()), Role.USER, 1);
        customerDocument.setAccountId(accountDocument.getId());
        customerRepository.save(customerDocument);
    }

    public void updateCustomerInfo(String id, UpdateCustomerModel userModel) throws BizException {
        CustomerDocument customerDocument = customerRepository.findById(id).orElseThrow(() -> new BizException("User info not found"));
        accountRepository.findById(customerDocument.getAccountId()).orElseThrow(() -> new BizException("Invalid id"));
        customerDocument.setName(userModel.getName());
        customerDocument.setPhoneNumber(userModel.getPhone());
        customerDocument.setAddress(userModel.getAddress());
    }

    public void deleteAccount(String id) throws BizException {
        CustomerDocument customer = customerRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        customer.setDeletedAd(ZonedDateTime.now());
        customerRepository.save(customer);
    }
}
