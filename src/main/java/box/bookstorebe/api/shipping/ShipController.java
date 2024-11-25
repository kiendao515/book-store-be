package box.bookstorebe.api.shipping;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.ghtk.OrderDetail;
import box.bookstorebe.model.order.ShippingFeeRequest;
import box.bookstorebe.service.shipping.ShipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;

@RestController
@RequestMapping("/api/v1/shipping")
@RequiredArgsConstructor
public class ShipController {
    private final ShipService shipService;

    @PostMapping("/fee")
    public BaseResponse<BigDecimal> calculateShippingFee(@RequestBody @Valid ShippingFeeRequest request){
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.calculateShippingFee(request));
    }

    @GetMapping()
    public BaseResponse<OrderDetail> getDetailOrder(@RequestParam String id){
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.getOrderDetail(id));
    }


}
