package box.bookstorebe.service.auth;

import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.user.PasswordResetToken;
import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.document.user.VerificationToken;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.user.UserDto;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.ChangePasswordRequestModel;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.user.PasswordResetTokenRepository;
import box.bookstorebe.repository.user.UserRepository;
import box.bookstorebe.repository.user.VerificationTokenRepository;
import box.bookstorebe.service.BaseService;
import box.bookstorebe.service.common.MailService;
import box.bookstorebe.service.user.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.mail.MailAuthenticationException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
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
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;
    private final MailService mailService;
    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository verificationTokenRepository;
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

        UserDocument user = userService.createUser(userModel);
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, user, clientUrl));
        return "Register successfully. Please check your email to confirm your account.";
    }

    public AuthResponseDto login(LoginRequestModel request) throws BizException {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDocument user = userRepository.findByEmail(request.getEmail()).orElseThrow(() -> new BizException("Invalid email/password"));
        if (!user.isEnabled()) {
            throw new BizException("Please confirm your email");
        }
        String jwtToken = jwtService.generateToken(user);
        UserDto userDto = new UserDto();
        userDto.setId(user.getId());
        userDto.setEmail(user.getEmail());
        userDto.setFullName(user.getFullName());

        return new AuthResponseDto(jwtToken, userDto);
    }

    public String confirmRegistration(String token) throws BizException {
        VerificationToken verificationToken = verificationTokenRepository.findByToken(token);
        if (verificationToken == null) throw new BizException("Invalid token");


        UserDocument user = userRepository.findById(verificationToken.getUserId()).orElseThrow(() -> new BizException("Invalid user"));
        if (verificationToken.getExpiryDate().isBefore(ZonedDateTime.now())) throw new BizException("Token expired");
        user.setEnabled(true);
        userRepository.save(user);
        return "Confirm registration successfully";
    }

    public String sendResetPassword(String email) throws BizException {
        UserDocument user = userRepository.findByEmail(email).orElseThrow(() -> new BizException("Invalid email"));
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
        UserDocument user = userRepository.findById(passwordResetToken.getUserId()).orElseThrow(() -> new BizException("Invalid user"));
        user.setPassword(passwordEncoder.encode(password));
        userRepository.save(user);
        return "Change password successfully";
    }

    public String changePassword(ChangePasswordRequestModel model) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }

        UserDocument user = userRepository.findById(currentUser.getUserId()).orElseThrow(() -> new BizException("Invalid user"));
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(user.getEmail(), model.getOldPassword())
        );
        user.setPassword(passwordEncoder.encode(model.getNewPassword()));
        userRepository.save(user);
        return "Change password successfully";
    }

    public void createVerificationTokenForUser(UserDocument user, String token) {
        VerificationToken verificationToken = new VerificationToken(token, user.getId());
        verificationTokenRepository.save(verificationToken);
    }

    public void createPasswordResetTokenForUser(UserDocument user, String token) {
        PasswordResetToken passwordResetToken = new PasswordResetToken(token, user.getId());
        passwordResetTokenRepository.save(passwordResetToken);
    }


}
