package box.bookstorebe.configuration;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.document.bookstore.StoreDocument;
import box.bookstorebe.repository.bookstore.BookStoreRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.account.AccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
@AllArgsConstructor
public class AdminAccountGenerate {
    private final AccountRepository accountRepository;
    private final PasswordEncoder passwordEncoder;
    private final BookStoreRepository storeRepository;

    @Bean
    public CommandLineRunner initAdminAccount() {
        String salt = AccountService.generateSalt();
        String password = salt + Const.ADMIN_PASS;
        return args -> {
            if (accountRepository.findByEmail(Const.ADMIN_EMAIL).isEmpty()) {
                AccountDocument admin = new AccountDocument();
                admin.setEmail(Const.ADMIN_EMAIL);
                admin.setSalt(salt);
                admin.setPassword(passwordEncoder.encode(password));
                admin.setRole(Role.ADMIN);
                admin.setEnabled(1);
                AccountDocument acc = accountRepository.save(admin);
                StoreDocument storeDocument = storeRepository.findByAccountId(acc.getId());
                if (storeDocument == null) {
                    storeDocument = new StoreDocument();
                    storeDocument.setAccountId(acc.getId());
                    storeDocument.setAddress("ngách 21 Ng.238 Đ.Âu Cơ, Quảng An, Tây Hồ, Hà Nội, Việt Nam");
                    storeDocument.setAddress("Nhà bán Hộp");
                    storeDocument.setPhoneNumber("098 220 36 56");
                }
                System.out.println("Tài khoản admin đã được tạo.");
            } else {
                System.out.println("Tài khoản admin đã tồn tại.");
            }
        };
    }
}
