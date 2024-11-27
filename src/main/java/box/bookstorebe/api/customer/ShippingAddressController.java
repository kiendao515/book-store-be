package box.bookstorebe.api.customer;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.customer.ShippingAddressDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.user.ShippingAddressModel;
import box.bookstorebe.service.partner.ShippingAddressService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/shipping-addresses")
@RequiredArgsConstructor
public class ShippingAddressController {
    private final ShippingAddressService shippingAddressService;

    @GetMapping()
    public BasePagingResponse<ShippingAddressDto> getAllShippingAddresses(
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size,
            @RequestParam(value = "user_id", required = false) String userId
    ) throws BizException {
        return new BasePagingResponse<>(shippingAddressService.getShippingAddress(userId, page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<ShippingAddressDto> getShippingAddressDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, shippingAddressService.getShippingAddressById(id));
    }

    @PostMapping()
    public BaseResponse<String> createNewShippingAddress(@RequestBody @Valid ShippingAddressModel shippingAddressModel) throws BizException {
        shippingAddressService.createShippingAddress(shippingAddressModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new shipping address successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateShippingAddress(@PathVariable String id, @RequestBody @Valid ShippingAddressModel shippingAddressModel) throws BizException {
        shippingAddressService.updateShippingAddress(id, shippingAddressModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update shipping address successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteShippingAddress(@PathVariable String id) throws BizException {
        shippingAddressService.deleteShippingAddress(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete shipping address successfully");
    }
}
