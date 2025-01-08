package box.bookstorebe.service.auth;

import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.AccountDocument;
import box.bookstorebe.document.account.CustomerDocument;
import box.bookstorebe.document.account.PasswordResetToken;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.auth.UserProfileDto;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.ChangePasswordRequestModel;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.auth.UpdateUserInfoModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.customer.CustomerRepository;
import box.bookstorebe.repository.user.AccountRepository;
import box.bookstorebe.repository.user.PasswordResetTokenRepository;
import box.bookstorebe.service.BaseService;
import box.bookstorebe.service.account.AccountService;
import box.bookstorebe.service.common.MailService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;
import java.util.UUID;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Service
@Slf4j
@RequiredArgsConstructor
public class AuthService extends BaseService {
    private final AccountRepository accountRepository;
    private final CustomerRepository customerRepository;
    private final JwtService jwtService;
    private final AccountService accountService;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final PasswordResetTokenRepository passwordResetTokenRepository;
    private final JavaMailSenderImpl mailSender;
    private final PasswordEncoder passwordEncoder;
    private final ExecutorService executor = Executors.newFixedThreadPool(10);
    @Value("${app.client.url}")
    private String clientUrl;

    public String register(RegisterRequestModel request) throws BizException {
        UserModel userModel = new UserModel();
        userModel.setEmail(request.getEmail());
        userModel.setPassword(request.getPassword());
        userModel.setFullName(request.getFullName());

        AccountDocument user = accountService.createAccount(userModel, Role.USER, 0);
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, user, clientUrl));
        return "Register successfully. Please check your email to confirm your account.";
    }

    public AuthResponseDto login(LoginRequestModel request) throws BizException {
        AccountDocument user = accountRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BizException("thông tin tài khoản hoặc mật khẩu không chính xác"));
        if (user.getDeletedAt() != null) {
            throw new BizException("account deleted");
        }
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
            );
        } catch (Exception e) {
            throw new BizException("thông tin tài khoản hoặc mật khẩu không chính xác");
        }

        if (user.getEnabled() == 0) {
            if(user.getRole().equals(Role.USER)) {
                throw new BizException("tài khoản chưa được xác thực. Vui lòng kiểm tra email để xác thực tài khoản");
            }else{
               throw new BizException("Tài khoản chưa đuợc xác thực. Vui lòng liên hệ người quan tri");
            }
        }
        String jwtToken = jwtService.generateToken(user);
        AccountDto userDto = new AccountDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setRole(user.getRole());
        userDto.setEmail(user.getEmail());
        if (user.getRole() == Role.USER) {
            CustomerDocument customer = customerRepository.findByAccountId(user.getId());
            userDto.setPoint(customer.getPoint());
            userDto.setName(customer.getName());
            userDto.setPhone(customer.getPhoneNumber());
            userDto.setAvatar(customer.getAvatar());
        }

        return new AuthResponseDto(jwtToken, userDto);
    }

    public String confirmRegistration(String token) throws BizException {
        AccountDocument accountDocument = accountRepository.findByToken(token).orElseThrow(() -> new BizException("invalid token"));
        if (accountDocument.getExpiryDate().isBefore(ZonedDateTime.now())) throw new BizException("Token expired");
        accountDocument.setEnabled(1);
        accountRepository.save(accountDocument);
        return "xác thực tài khoản thành công";
    }

    public String sendResetPassword(String email) throws BizException {
        AccountDocument user = accountRepository.findByEmail(email).orElseThrow(() -> new BizException("Invalid email"));
        String token = UUID.randomUUID().toString();
        this.createPasswordResetTokenForUser(user, token);
        try {
            SimpleMailMessage emailMessage = mailService.constructResetTokenEmail(clientUrl, token, user, "You have requested to reset your password");
            executor.execute(() -> {
                mailSender.send(emailMessage);
            });
        } catch (MailAuthenticationException e) {
            log.error("Error sending reset password email to user: {}", user.getEmail());
            throw new BizException("Error sending reset password email to user: " + user.getEmail());
        } catch (Exception e) {
            log.error("Some error when sending reset password email: {}", e.getMessage());
            throw new BizException("Error sending reset password email to user: " + user.getEmail());
        }
        return "Send reset password email successfully";
    }

    public String resetPassword(String token, String password) throws BizException {
        PasswordResetToken passwordResetToken = passwordResetTokenRepository.findByToken(token);
        if (passwordResetToken == null) throw new BizException("Invalid token");
        if (passwordResetToken.getExpiryDate().isBefore(ZonedDateTime.now())) throw new BizException("Token expired");
        AccountDocument user = accountRepository.findById(passwordResetToken.getUserId()).orElseThrow(() -> new BizException("Invalid user"));

        user.setPassword(passwordEncoder.encode(password));
        accountRepository.save(user);
        return "Change password successfully";
    }

    public String changePassword(ChangePasswordRequestModel model) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }

        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid user"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), model.getOldPassword())
        );
        user.setPassword(passwordEncoder.encode(model.getNewPassword()));
        accountRepository.save(user);
        return "Change password successfully";
    }

    public void createVerificationTokenForUser(AccountDocument user, String token) {
        user.setToken(token);
        accountRepository.save(user);
    }

    public void createPasswordResetTokenForUser(AccountDocument user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user.getId());
        passwordResetTokenRepository.save(passwordResetToken);
    }

    public UserProfileDto getUserProfile() throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        AccountDocument user = accountRepository.findById(currentUser.getAccountId()).orElseThrow(() -> new BizException("Invalid user"));
        if (currentUser.getRole().equals(Role.USER)) {
            CustomerDocument customerDocument = customerRepository.findByAccountId(user.getId());
            UserProfileDto userProfileDto = new UserProfileDto();
            userProfileDto.setId(user.getId());
            userProfileDto.setEmail(user.getEmail());
            if (customerDocument != null) {
                userProfileDto.setDateOfBirth(customerDocument.getDateOfBirth());
                userProfileDto.setPhoneNumber(customerDocument.getPhoneNumber());
                userProfileDto.setFullName(customerDocument.getName());
                userProfileDto.setPoint(customerDocument.getPoint());
            }
            userProfileDto.setRole(user.getRole().name());
            return userProfileDto;
        } else if (currentUser.getRole().equals(Role.ADMIN) || currentUser.getRole().equals(Role.STORE)) {
            UserProfileDto userProfileDto = new UserProfileDto();
            userProfileDto.setId(user.getId());
            userProfileDto.setEmail(user.getEmail());
            userProfileDto.setRole(user.getRole().name());
            return userProfileDto;
        }
        return null;
    }

    public UserProfileDto updateUserInfo(UpdateUserInfoModel userInfoModel) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        CustomerDocument user = customerRepository.findByAccountId(currentUser.getAccountId());
        if (user == null) {
            user = new CustomerDocument();
            user.setAccountId(currentUser.getAccountId());
        }
        user.setName(userInfoModel.getFullName());
        user.setPhoneNumber(userInfoModel.getPhoneNumber());
        user.setDateOfBirth(userInfoModel.getDateOfBirth());
        customerRepository.save(user);
        return UserProfileDto.builder()
                .id(user.getId())
                .email(currentUser.getEmail())
                .fullName(user.getName())
                .phoneNumber(user.getPhoneNumber())
                .dateOfBirth(user.getDateOfBirth())
                .role(currentUser.getRole().name())
                .build();

    }

}
