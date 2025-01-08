package box.bookstorebe.service.account;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.document.account.ShippingAddressDocument;
import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.account.AccountMapper;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.customer.CustomerRepository;
import box.bookstorebe.repository.order.OrderRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.repository.user.ShippingAddressRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.ZonedDateTime;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Slf4j
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final CustomerRepository customerRepository;
    private final BookStoreRepository storeRepository;
    private final ShippingAddressRepository shippingAddressRepository;
    private final OrderRepository orderRepository;

    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public Page<AccountDto> getAccounts(String q,String role, String email, Integer page, Integer size) {
        Page<AccountDto> accounts = accountRepository.getUsers(role, email, page, size);
        List<String> listAccountId = accounts.stream().map(AccountDto::getId).toList();
        if ("USER".equals(role)) {
            List<CustomerDocument> customerDocuments;
            if (q != null && !q.isEmpty()) {
                // Filter customers matching the search query
                customerDocuments = customerRepository.findAllByAccountIdIn(listAccountId).stream()
                        .filter(customer -> customer.getName().toLowerCase().contains(q.toLowerCase()))
                        .collect(Collectors.toList());
            } else {
                // Fetch all customers if query is null or empty
                customerDocuments = customerRepository.findAllByAccountIdIn(listAccountId);
            }

            Map<String, CustomerDocument> customerMap = customerDocuments.stream()
                    .collect(Collectors.toMap(CustomerDocument::getAccountId, customer -> customer));

// Convert account content to a modifiable list
            List<AccountDto> accountContent = new ArrayList<>(accounts.getContent());

            accountContent.removeIf(account -> {
                CustomerDocument customer = customerMap.get(account.getId());
                if (customer == null) {
                    return true;
                } else {
                    account.setAvatar(customer.getAvatar());
                    account.setName(customer.getName());
                    account.setPhone(customer.getPhoneNumber());
                    account.setCreatedAt(customer.getCreatedAt());
                    return false;
                }
            });

            List<String> accountIds = accountContent.stream()
                    .map(AccountDto::getId)
                    .collect(Collectors.toList());

// Fetch default addresses
            List<ShippingAddressDocument> addresses = shippingAddressRepository.findAllByUserIdInAndIsDefault(accountIds, true);
            Map<String, ShippingAddressDocument> addressMap = addresses.stream()
                    .collect(Collectors.toMap(ShippingAddressDocument::getUserId, address -> address));

// Fetch completed orders
            List<OrderDocument> completedOrders = orderRepository.findAllByAccountIdInAndStatus(accountIds, Const.OrderStatus.DONE);
            Map<String, Long> completedOrderCountMap = completedOrders.stream()
                    .collect(Collectors.groupingBy(OrderDocument::getAccountId, Collectors.counting()));

// Process accounts for additional data
            accountContent.forEach(account -> {
                ShippingAddressDocument address = addressMap.get(account.getId());
                if (address != null) {
                    account.setAddress(
                            address.getStreet() + ", " +
                                    address.getWard().getFullName() + ", " +
                                    address.getDistrict().getFullName() + ", " +
                                    address.getProvince().getFullName()
                    );
                }

                Long completedOrderCount = completedOrderCountMap.getOrDefault(account.getId(), 0L);
                account.setOrdersCompleted(completedOrderCount);
            });

// Re-create Page with modified content
            accounts = new PageImpl<>(accountContent, accounts.getPageable(), accounts.getTotalElements());


        }
        if ("STORE".equals(role)) {
            List<StoreDocument> storeDocuments = storeRepository.findAllByAccountIdInAndDeletedAtIsNull(listAccountId);
            Map<String, StoreDocument> storeDocumentMap = storeDocuments.stream()
                    .collect(Collectors.toMap(StoreDocument::getAccountId, store -> store));
            accounts.forEach(account -> {
                StoreDocument customer = storeDocumentMap.get(account.getId());
                if (customer != null) {
                    account.setAvatar(customer.getThumbnail());
                    account.setName(customer.getName());
                    account.setPhone(customer.getPhoneNumber());
                    account.setAddress(customer.getAddress());
                    account.setDescription(customer.getDescription());
                    account.setCommissionPercentage(customer.getCommissionPercentage());
                    account.setCreatedAt(customer.getCreatedAt());
                }
            });
        }
        return accounts;
    }

    public AccountDto getAccountDetail(String id) throws BizException {
        AccountDocument user = accountRepository.findById(id).orElseThrow(() -> new BizException("User not found"));
        return AccountMapper.INSTANCE.entityToDto(user);
    }

    public AccountDocument createAccount(UserModel userModel, Role role, Integer isEnable) throws BizException {
        AccountDocument user = accountRepository.findByEmail(userModel.getEmail()).orElseGet(() -> null);
        if (user != null) {
            throw new BizException("Email already exists");
        }

        AccountDocument newUser = new AccountDocument();
        newUser.setEmail(userModel.getEmail());
        String salt = generateSalt();
        String combinedPasswordSalt = salt + userModel.getPassword();
        newUser.setPassword(passwordEncoder.encode(combinedPasswordSalt));
        newUser.setSalt(salt);
        newUser.setRole(role);
        newUser.setEnabled(isEnable);
        newUser.setExpiryDate(calculateExpiryDate(60 * 24));
        newUser.setCreatedAt(ZonedDateTime.now());
        AccountDocument accountDocument = accountRepository.save(newUser);
        if (role.equals(Role.USER)){
            CustomerDocument customerDocument = new CustomerDocument();
            customerDocument.setName(userModel.getFullName());
            customerDocument.setAccountId(accountDocument.getId());
            customerDocument.setCreatedAt(ZonedDateTime.now());
            customerRepository.save(customerDocument);
        }
        return accountDocument;
    }
    public AccountDocument resetPass(String id, UserModel userModel) throws BizException {
        AccountDocument user = accountRepository.findById(id).orElseThrow(()-> new BizException("invalid id"));
        String salt = generateSalt();
        String combinedPasswordSalt = salt + userModel.getPassword();
        user.setPassword(passwordEncoder.encode(combinedPasswordSalt));
        user.setSalt(salt);
        user.setUpdatedAt(ZonedDateTime.now());
        return accountRepository.save(user);
    }

    private ZonedDateTime calculateExpiryDate(int expiryTimeInMinutes) {
        return ZonedDateTime.now().plusMinutes(expiryTimeInMinutes);
    }

    public void updateAccount(String id, UserModel userModel) throws BizException {
        AccountDocument updatedUser = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));

        AccountDocument user = accountRepository.findByEmail(userModel.getEmail()).orElseThrow(() -> new BizException("email ko tồn tại"));
        if (user != null && !user.getId().equals(id)) {
            throw new BizException("Email already exists");
        }

        updatedUser.setEmail(userModel.getEmail());
        if (user != null && user.getSalt() == null) {
            throw new BizException("user account missing salt");
        }
        String combinedPasswordSalt = user.getSalt() + userModel.getPassword();
        updatedUser.setPassword(passwordEncoder.encode(combinedPasswordSalt));
        updatedUser.setRole(Role.USER);
        updatedUser.setUpdatedAt(ZonedDateTime.now());
        accountRepository.save(updatedUser);
    }

    public void deleteAccount(String id) throws BizException {
        AccountDocument user = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        user.setDeletedAt(ZonedDateTime.now());
        CustomerDocument customerDocument = customerRepository.findByAccountId(user.getId());
        if(customerDocument != null){
            customerDocument.setDeletedAt(ZonedDateTime.now());
            customerRepository.save(customerDocument);
        }
        StoreDocument storeDocument = storeRepository.findByAccountId(id);
        if(storeDocument != null){
            storeDocument.setDeletedAt(ZonedDateTime.now());
            storeRepository.save(storeDocument);
        }
        accountRepository.save(user);
    }
}
