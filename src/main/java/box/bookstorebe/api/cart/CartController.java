package box.bookstorebe.api.cart;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.order.CartDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateCartModel;
import box.bookstorebe.model.order.CreateOrderModel;
import box.bookstorebe.service.order.CartService;
import jakarta.mail.MessagingException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/carts")
@RequiredArgsConstructor
public class CartController {
    private final CartService cartService;
    @PostMapping
    public BaseResponse<String> addToCart(@RequestBody @Valid CreateCartModel cartModel, HttpServletRequest request) throws BizException, MessagingException {
        cartService.saveCart(cartModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Thêm vào giỏ hàng thành công!");
    }

    @GetMapping
    public BaseResponse<List<CartDto>> getCart() throws BizException {
        List<CartDto> cartDto = cartService.getCarts();
        return new BaseResponse<>(Const.ResultCode.SUCCESS, cartDto);
    }
}
