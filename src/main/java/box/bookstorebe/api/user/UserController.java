package box.bookstorebe.api.user;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.user.UserDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.service.auth.AuthService;
import box.bookstorebe.service.user.UserService;
import jakarta.validation.Valid;
import jakarta.websocket.server.PathParam;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    @GetMapping()
    public BasePagingResponse<UserDto> getAllUsers(
            @RequestParam(name = "email", required = false) String email,
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return new BasePagingResponse<>(userService.getUsers(email, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<UserDto> getUserDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, userService.getUserDetail(id));
    }

    @PostMapping()
    public BaseResponse<String> createNewUser(@RequestBody @Valid UserModel userModel) throws BizException {
        userService.createUser(userModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new user successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateUser(@PathVariable String id, @RequestBody @Valid UserModel userModel) throws BizException {
        userService.updateUser(id, userModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update user successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteUser(@PathVariable String id) throws BizException {
        userService.deleteUser(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete user successfully");
    }
}
