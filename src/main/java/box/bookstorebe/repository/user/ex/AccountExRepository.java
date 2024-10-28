package box.bookstorebe.repository.user.ex;

import box.bookstorebe.dto.account.AccountDto;
import org.springframework.data.domain.Page;

public interface AccountExRepository {
    Page<AccountDto> getUsers(String email, Integer page, Integer size);
}
