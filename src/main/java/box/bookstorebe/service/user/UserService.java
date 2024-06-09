package box.bookstorebe.service.user;

import box.bookstorebe.document.user.Role;
import box.bookstorebe.document.user.UserDocument;
import box.bookstorebe.dto.user.UserDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.mapper.user.UserMapper;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.repository.user.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@Slf4j
@AllArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public Page<UserDto> getUsers(String email, int page, int size) {
        return userRepository.getUsers(email, page, size);
    }

    public UserDto getUserDetail(String id) throws BizException {
        UserDocument user = userRepository.findById(id).orElseThrow(() -> new BizException("User not found"));
        return UserMapper.INSTANCE.entityToDto(user);
    }

    public UserDocument createUser(UserModel userModel) throws BizException {
        UserDocument user = userRepository.findByEmail(userModel.getEmail()).orElseGet(() -> null);
        if (user != null) {
            throw new BizException("Email already exists");
        }

        UserDocument newUser = new UserDocument();
        newUser.setEmail(userModel.getEmail());
        newUser.setPassword(passwordEncoder.encode(userModel.getPassword()));
        newUser.setFirstName(userModel.getFirstName());
        newUser.setLastName(userModel.getLastName());
        newUser.setRole(Role.USER);
        return userRepository.save(newUser);
    }

    public void updateUser(String id, UserModel userModel) throws BizException {
        UserDocument updatedUser = userRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));

        UserDocument user = userRepository.findByEmail(userModel.getEmail()).orElseGet(() -> null);
        if (user != null && !user.getId().equals(id)) {
            throw new BizException("Email already exists");
        }

        updatedUser.setEmail(userModel.getEmail());
        updatedUser.setPassword(passwordEncoder.encode(userModel.getPassword()));
        updatedUser.setFirstName(userModel.getFirstName());
        updatedUser.setLastName(userModel.getLastName());
        updatedUser.setRole(Role.USER);
        userRepository.save(updatedUser);
    }

    public void deleteUser(String id) throws BizException {
        UserDocument user = userRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        userRepository.delete(user);
    }
}
