package box.bookstorebe.api.common;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.dto.common.DistrictDto;
import box.bookstorebe.dto.common.ProvinceDto;
import box.bookstorebe.dto.common.WardDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.service.common.AddressService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
@RequiredArgsConstructor
public class AddressController {
    private final AddressService provinceService;

    @GetMapping("/provinces")
    public BaseResponse<List<ProvinceDto>> getProvinces() {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, provinceService.getProvinces());
    }

    @GetMapping("districts")
    public BaseResponse<List<DistrictDto>> getDistricts(@RequestParam(name = "province_code") String provinceCode) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, provinceService.getDistricts(provinceCode));
    }

    @GetMapping("wards")
    public BaseResponse<List<WardDto>> getWards(@RequestParam(name = "district_code") String districtCode) throws BizException {
        return new BaseResponse<>(Const.ResultCode.SUCCESS, provinceService.getWards(districtCode));
    }

}
