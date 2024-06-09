package box.bookstorebe.api.auth;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.service.auth.AuthService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping("/register")
    public BaseResponse<AuthResponseDto> register(@RequestBody RegisterRequestModel request) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.register(request));
    }

    @PostMapping("/login")
    public BaseResponse<AuthResponseDto> login(@RequestBody LoginRequestModel request) {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.login(request));
    }
}
