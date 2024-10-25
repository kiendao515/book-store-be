package box.bookstorebe.configuration;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.service.account.AccountService;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.security.SecureRandom;
import java.util.Base64;

@Configuration
@AllArgsConstructor
public class AdminAccountGenerate {
    private final AccountRepository accountRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final AccountService accountService;
    public CommandLineRunner initAdminAccount() {
        String salt = accountService.generateSalt();
        return args -> {
            if (accountRepository.findByEmail("admin").isEmpty()) {
                AccountDocument admin = new AccountDocument();
                admin.setEmail("admin");
                admin.setPassword(passwordEncoder.encode("admin123")); // Đổi mật khẩu phù hợp
                admin.setRole(Role.ADMIN);
                accountRepository.save(admin);
                System.out.println("Tài khoản admin đã được tạo.");
            } else {
                System.out.println("Tài khoản admin đã tồn tại.");
            }
        };
    }
}
