package box.bookstorebe.service.auth;

import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.document.user.VerificationToken;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.eventlistener.event.OnRegistrationCompleteEvent;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.user.UserRepository;
import box.bookstorebe.repository.user.VerificationTokenRepository;
import box.bookstorebe.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.time.ZonedDateTime;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;

    private final AuthenticationManager authenticationManager;
    private final VerificationTokenRepository verificationTokenRepository;
    private final ApplicationEventPublisher applicationEventPublisher;

    public String register(RegisterRequestModel request) throws BizException {
        UserModel userModel = new UserModel();
        userModel.setEmail(request.getEmail());
        userModel.setPassword(request.getPassword());
        userModel.setFirstName(request.getFirstName());
        userModel.setLastName(request.getLastName());

        UserDocument user = userService.createUser(userModel);
        String appUrl = "http://localhost:8080/api/v1/auth";
        applicationEventPublisher.publishEvent(new OnRegistrationCompleteEvent(this, user, appUrl));
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
        return new AuthResponseDto(jwtToken);
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

    public void createVerificationTokenForUser(UserDocument user, String token) throws BizException {
        VerificationToken verificationToken = new VerificationToken(token, user.getId());
        verificationTokenRepository.save(verificationToken);
    }

}
