package box.bookstorebe.service.partner;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.account.DeleteAccountDto;
import box.bookstorebe.dto.customer.CustomerInfoDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.account.AccountMapper;
import box.bookstorebe.model.user.CustomerModel;
import box.bookstorebe.model.user.UpdateCustomerModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.customer.CustomerRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.account.AccountService;
import jakarta.mail.Address;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.List;

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

    public CustomerDocument createCustomerInfo(UpdateCustomerModel customer) throws BizException {
        CustomerDocument customerDocument = new CustomerDocument();
        customerDocument.setName(customer.getName());
        List<String> address = new ArrayList<>();
        address.add(customer.getAddress());
        customerDocument.setAddress(address);
        customerDocument.setPhoneNumber(customer.getPhone());
        return customerDocument;
    }

//    public void createCustomerInfoAndAccount(CustomerModel customerModel) throws BizException {
//        CustomerDocument customerDocument = new CustomerDocument();
//        List<String> address = new ArrayList<>();
//        address.add(customerModel.getAddress());
//        customerDocument.setAddress(address);
//        customerDocument.setPhoneNumber(customerModel.getPhone());
//        customerDocument.setName(customerModel.getName());
//        customerDocument.setAvatar(customerModel.getAvatar());
//        AccountDocument accountDocument = accountService.createAccount(new UserModel(customerModel.getEmail(), customerModel.getPassword()), Role.USER, 1);
//        customerDocument.setAccountId(accountDocument.getId());
//        customerRepository.save(customerDocument);
//    }

    @Transactional(rollbackFor = Exception.class)
    public void updateCustomerInfo(String id, UpdateCustomerModel userModel) throws BizException {
        AccountDocument accountDocument = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        CustomerDocument customerDocument = customerRepository.findByAccountId(accountDocument.getId());
        if(userModel.getEnabled() != null){
            if(!userModel.getEnabled().equals("0") && !userModel.getEnabled().equals("1")){
                throw new BizException("invalid enabled param");
            }
            accountDocument.setEnabled(Integer.parseInt(userModel.getEnabled()));
            accountRepository.save(accountDocument);
        }
        if (customerDocument == null) {
            customerDocument = createCustomerInfo(userModel);
            customerDocument.setAccountId(accountDocument.getId());
        }else{
            if (userModel.getName() != null) {
                customerDocument.setName(userModel.getName());
            }
            if (userModel.getAddress() != null) {
                List<String> address = new ArrayList<>();
                address.add(userModel.getAddress());
                customerDocument.setAddress(address);
            }
            if (userModel.getPhone() != null) {
                customerDocument.setPhoneNumber(userModel.getPhone());
            }
            if (userModel.getAvatar() != null) {
                customerDocument.setAvatar(userModel.getAvatar());
            }
        }
        customerRepository.save(customerDocument);
    }

    public void deleteAccount(String id) throws BizException {
        CustomerDocument customer = customerRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        customer.setDeletedAt(ZonedDateTime.now());
        customerRepository.save(customer);
    }

    public void deleteAccountAndCustomerInfo(DeleteAccountDto deleteAccountDto) throws BizException {
        for(String id : deleteAccountDto.getAccountIds()){
            AccountDocument accountDocument = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid account id"));
            accountDocument.setDeletedAt(ZonedDateTime.now());
            accountRepository.save(accountDocument);
            CustomerDocument customer = customerRepository.findByAccountId(id);
            if(customer != null){
                customer.setDeletedAt(ZonedDateTime.now());
                customerRepository.save(customer);
            }
        }
    }
}
