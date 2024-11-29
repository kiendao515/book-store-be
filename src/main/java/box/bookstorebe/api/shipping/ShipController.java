package box.bookstorebe.api.shipping;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.ghtk.GhtkOrderDto;
import box.bookstorebe.dto.ghtk.OrderDetail;
import box.bookstorebe.dto.ghtk.PickAddressDto;
import box.bookstorebe.model.order.CreateOrder;
import box.bookstorebe.model.order.LabelRequest;
import box.bookstorebe.model.order.ShippingFeeRequest;
import box.bookstorebe.service.shipping.ShipService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;

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

    @GetMapping("address/pick")
    public BaseResponse<List<PickAddressDto.PickupData>> getListPickAddress(){
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.getListPickAddress());
    }
    @PostMapping("/order/create")
    public BaseResponse<GhtkOrderDto.OrderResult> printLabel(@RequestBody @Valid CreateOrder request){
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.createGhtkOrder(request));
    }
//    @PostMapping("/label")
//    public BaseResponse<BigDecimal> printLabel(@RequestBody @Valid LabelRequest request){
//        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.calculateShippingFee(request));
//    }



}
