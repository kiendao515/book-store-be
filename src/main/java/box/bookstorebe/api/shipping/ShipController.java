package box.bookstorebe.api.shipping;

import box.bookstorebe.common.Const;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.ghtk.GhtkOrderDto;
import box.bookstorebe.dto.ghtk.OrderDetail;
import box.bookstorebe.dto.ghtk.PickAddressDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateOrder;
import box.bookstorebe.model.order.LabelRequest;
import box.bookstorebe.model.order.ShippingFeeRequest;
import box.bookstorebe.repository.order.OrderRepository;
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
    private final OrderRepository orderRepository;

    @PostMapping("/fee")
    public BaseResponse<BigDecimal> calculateShippingFee(@RequestBody @Valid ShippingFeeRequest request) {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.calculateShippingFee(request));
    }

    @GetMapping()
    public BaseResponse<OrderDetail> getDetailOrder(@RequestParam String id) {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.getOrderDetail(id));
    }

    @GetMapping("address/pick")
    public BaseResponse<List<PickAddressDto.PickupData>> getListPickAddress() {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.getListPickAddress());
    }

    @PostMapping("/order/create")
    public BaseResponse<List<GhtkOrderDto.OrderResult>> printLabel(@RequestBody @Valid List<CreateOrder> request) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.createGhtkOrders(request));
    }

    @PostMapping("/order/combine/create")
    public BaseResponse<GhtkOrderDto.OrderResult> createOrder(@RequestBody @Valid CreateOrder request) throws BizException {
     return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.createCombinedOrder(request));
    }

    @GetMapping("/label")
    public BaseResponse<byte[]> printLabel(@RequestParam @Valid String orderId) throws BizException {
        OrderDocument orderDocument = orderRepository.findByOrderCode(orderId);
        if(orderDocument.getShippingCode()==null){
            throw new BizException("Đơn chưa được gửi");
        }
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shipService.printOrder(orderDocument.getShippingCode()));
    }


}
