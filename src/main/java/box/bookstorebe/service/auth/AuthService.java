package box.bookstorebe.service.auth;

import box.bookstorebe.document.user.Role;
import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.user.UserRepository;
import box.bookstorebe.service.user.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class AuthService {
    private final PasswordEncoder passwordEncoder;
    private final UserRepository userRepository;
    private final JwtService jwtService;
    private final UserService userService;

    private final AuthenticationManager authenticationManager;

    public AuthResponseDto register(RegisterRequestModel request) throws BizException {
        UserModel userModel = new UserModel();
        userModel.setEmail(request.getEmail());
        userModel.setPassword(request.getPassword());
        userModel.setFirstName(request.getFirstName());
        userModel.setLastName(request.getLastName());

        UserDocument user = userService.createUser(userModel);
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken);
    }

    public AuthResponseDto login(LoginRequestModel request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword())
        );

        UserDocument user = userRepository.findByEmail(request.getEmail()).orElseThrow();
        String jwtToken = jwtService.generateToken(user);
        return new AuthResponseDto(jwtToken);
    }

}
