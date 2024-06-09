package box.bookstorebe.repository.user.ex;

import box.bookstorebe.dto.user.UserDto;
import org.springframework.data.domain.Page;

public interface UserExRepository {
    Page<UserDto> getUsers(String email, int page, int size);
}
