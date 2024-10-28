package box.bookstorebe.service.account;

import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.account.AccountMapper;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.user.AccountRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.util.Base64;

@Service
@Slf4j
@AllArgsConstructor
public class AccountService {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    public static String generateSalt() {
        SecureRandom random = new SecureRandom();
        byte[] saltBytes = new byte[16];
        random.nextBytes(saltBytes);
        return Base64.getEncoder().encodeToString(saltBytes);
    }

    public Page<AccountDto> getAccounts(String email, Integer page, Integer size) {
        return accountRepository.getUsers(email, page, size);
    }

    public AccountDto getAccountDetail(String id) throws BizException {
        AccountDocument user = accountRepository.findById(id).orElseThrow(() -> new BizException("User not found"));
        return AccountMapper.INSTANCE.entityToDto(user);
    }

    public AccountDocument createAccount(UserModel userModel,Role role) throws BizException {
        AccountDocument user = accountRepository.findByEmail(userModel.getEmail()).orElseGet(() -> null);
        if (user != null) {
            throw new BizException("Email already exists");
        }

        AccountDocument newUser = new AccountDocument();
        newUser.setEmail(userModel.getEmail());
        String salt = generateSalt();
        String combinedPasswordSalt= salt+ userModel.getPassword();
        newUser.setPassword(passwordEncoder.encode(combinedPasswordSalt));
        newUser.setRole(role);
        return accountRepository.save(newUser);
    }

    public void updateAccount(String id, UserModel userModel) throws BizException {
        AccountDocument updatedUser = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));

        AccountDocument user = accountRepository.findByEmail(userModel.getEmail()).orElseGet(() -> null);
        if (user != null && !user.getId().equals(id)) {
            throw new BizException("Email already exists");
        }

        updatedUser.setEmail(userModel.getEmail());
        String combinedPasswordSalt= user.getSalt() + userModel.getPassword();
        updatedUser.setPassword(passwordEncoder.encode(combinedPasswordSalt));
        updatedUser.setRole(Role.USER);
        accountRepository.save(updatedUser);
    }

    public void deleteAccount(String id) throws BizException {
        AccountDocument user = accountRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        accountRepository.delete(user);
    }
}
