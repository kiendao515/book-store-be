package box.bookstorebe.api.order;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.order.*;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateOfflineOrderModel;
import box.bookstorebe.service.order.OfflineOrderService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.ZonedDateTime;

@RestController
@RequestMapping("/api/v1/offline-orders")
@RequiredArgsConstructor
public class OfflineOrderController {
    private final OfflineOrderService offlineOrderService;

    @PostMapping
    @PreAuthorize("hasAnyAuthority('ADMIN')")
    public BaseResponse<String> createOfflineOrder(@RequestBody @Valid CreateOfflineOrderModel orderModel) throws BizException {
        offlineOrderService.createOfflineOrder(orderModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create offline order successfully");
    }

    @GetMapping()
    public BasePagingResponse<OfflineOrderDto> getOfflineOrders(
            @RequestParam(name = "start_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime startAt,
            @RequestParam(name = "end_at", required = false) @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) ZonedDateTime endAt,
            @RequestParam(name = "page", required = false) Integer page,
            @RequestParam(name = "size", required = false) Integer size
    ) {
        return new BasePagingResponse<>(offlineOrderService.getOfflineOrders(startAt, endAt, page, size));
    }

    @GetMapping("book-detail")
    public BaseResponse<OfflineBookDetailDto> getOfflineBookDetail(
            @RequestParam(name = "barcode", required = false) String barcode,
            @RequestParam(name = "quantity", required = false) Integer quantity
    ) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, offlineOrderService.getOfflineBookDetail(barcode, quantity));
    }

    @GetMapping("{id}")
    public BaseResponse<OfflineOrderDetailDto> getOfflineOrderDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, offlineOrderService.findById(id));
    }
}
