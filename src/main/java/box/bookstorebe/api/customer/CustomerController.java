package box.bookstorebe.api.customer;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.account.AccountDto;
import box.bookstorebe.dto.common.BasePagingResponse;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.customer.CustomerInfoDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.user.CustomerModel;
import box.bookstorebe.model.user.UpdateCustomerModel;
import box.bookstorebe.model.user.UserModel;
import box.bookstorebe.service.partner.CustomerService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/customers")
@RequiredArgsConstructor
public class CustomerController {
    private final CustomerService customerService;
    @GetMapping()
    public BasePagingResponse<CustomerInfoDto> getCustomerInfos(
            @RequestParam(name = "name", required = false) String name,
            @RequestParam(name = "phone", required = false) String phone,
            @RequestParam(name = "address", required = false) String address,
            @RequestParam(value = "page", required = false) Integer page,
            @RequestParam(value = "size", required = false) Integer size
    ) throws BizException {
        return new BasePagingResponse<>(customerService.getCustomerInfo(name, phone,address,page, size));
    }

    @GetMapping("{id}")
    public BaseResponse<CustomerInfoDto> getCustomerInfoDetail(@PathVariable String id) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, customerService.getCustomerInfoDetail(id));
    }

    @PostMapping()
    public BaseResponse<String> createNewCustomerInfoAndAccount(@RequestBody @Valid CustomerModel userModel) throws BizException {
        customerService.createCustomerInfoAndAccount(userModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Create new customer info and account successfully");
    }

    @PutMapping("{id}")
    public BaseResponse<String> updateCustomerInfo(@PathVariable String id, @RequestBody @Valid UpdateCustomerModel userModel) throws BizException {
        customerService.updateCustomerInfo(id, userModel);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Update customer info successfully");
    }

    @DeleteMapping("{id}")
    public BaseResponse<String> deleteUser(@PathVariable String id) throws BizException {
        customerService.deleteAccount(id);
        return new BaseResponse<>(Const.ResultCode.SUCCESS, "Delete user successfully");
    }
}
