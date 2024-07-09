package box.bookstorebe.api.order;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.auth.AuthResponseDto;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.auth.LoginRequestModel;
import box.bookstorebe.model.auth.RegisterRequestModel;
import box.bookstorebe.model.book.bookreality.CreateBookRealityModel;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.service.auth.AuthService;
import box.bookstorebe.service.order.OrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/orders")
@RequiredArgsConstructor
public class OrderController {
    private final OrderService orderService;

    @PostMapping("/create")
    public BaseResponse<String> createOrder(@RequestBody @Valid CreateOrderModel orderModel) throws BizException {
        orderService.createOrder(orderModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create order successfully");
    }


//    @PostMapping("/cancel")
//    public BaseResponse<AuthResponseDto> login(@RequestBody LoginRequestModel request) {
//        return new BaseResponse<>(Const.ResultCode.SUCCESS, authService.login(request));
//    }
}
