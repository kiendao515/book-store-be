package box.bookstorebe.api.auth;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.service.auth.AuthService;
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
}
