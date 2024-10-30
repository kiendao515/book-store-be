package box.bookstorebe.api.account;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.account.Role;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.service.account.AccountService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/accounts")
@RequiredArgsConstructor
public class AccountController {
    private final AccountService userService;
    // route này cho admin crud account khách hàng

    @GetMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BasePagingResponse<AccountDto> getAllUsers(
            @RequestParam(name = "role", required = false) String role,
            @RequestParam(name = "email", required = false) String email,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(userService.getAccounts(role, email, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<AccountDto> getUserDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, userService.getAccountDetail(id));
    }

    @PostMapping()
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<String> createNewUser(@RequestBody @Valid UserModel userModel) throws BizException {
        userService.createAccount(userModel, Role.USER,1);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new user successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateUser(@PathVariable String id, @RequestBody @Valid UserModel userModel) throws BizException {
        userService.updateAccount(id, userModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update user successfully");
    }

    @DeleteMapping("{id}")
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<String> deleteUser(@PathVariable String id) throws BizException {
        userService.deleteAccount(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete user successfully");
    }
}
