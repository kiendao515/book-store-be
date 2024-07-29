package box.bookstorebe.api.auth;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.*;
import box.bookstorebe.service.auth.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public BaseResponse<String> register(@RequestBody RegisterRequestModel request) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.register(request));
    }

    @PostMapping("/login")
    public BaseResponse<AuthResponseDto> login(@RequestBody LoginRequestModel request) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.login(request));
    }

    @GetMapping("/registration/confirm")
    public BaseResponse<String> confirmRegistration(@RequestParam("token") String token) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.confirmRegistration(token));
    }

    @PostMapping("/send-reset-password")
    public BaseResponse<String> sendResetPassword(@RequestBody SendResetPasswordRequestModel model) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.sendResetPassword(model.getEmail()));
    }

    @PostMapping("/reset-password")
    public BaseResponse<String> resetPassword(@RequestBody @Valid ResetPasswordRequestModel model) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.resetPassword(model.getToken(), model.getNewPassword()));
    }

    @PutMapping("/change-password")
    public BaseResponse<String> changePassword(@RequestBody @Valid ChangePasswordRequestModel model) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.changePassword(model));
    }

}
