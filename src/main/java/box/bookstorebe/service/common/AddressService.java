package box.bookstorebe.service.common;

import box.bookstorebe.document.common.ProvinceDocument;
import box.bookstorebe.dto.common.*;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.repository.common.province.ProvinceRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
@AllArgsConstructor
@Service
@Slf4j
public class AddressService {
    private final ProvinceRepository provinceRepository;

    public List<ProvinceDto> getProvinces() {
        List<ProvinceDocument> provinceDocuments = provinceRepository.getAllProvinces(null, null, null);
        List<ProvinceDto> result = new ArrayList<>();
        for (ProvinceDocument provinceDocument : provinceDocuments) {
            ProvinceDto provinceDto = new ProvinceDto();
            provinceDto.setCode(provinceDocument.getCode());
            provinceDto.setFullName(provinceDocument.getFullName());
            result.add(provinceDto);
        }
        return result;
    }

    public List<DistrictDto> getDistricts(String provinceCode) throws BizException {
        List<ProvinceDocument> provinceDocuments = provinceRepository.getAllProvinces(provinceCode, null, null);
        if (provinceDocuments.isEmpty()) {
            throw new BizException("Invalid province code");
        }
        List<ProvinceDocument.District> districts = provinceDocuments.get(0).getDistricts();
        List<DistrictDto> result = new ArrayList<>();
        for (ProvinceDocument.District district : districts) {
            DistrictDto districtDto = new DistrictDto();
            districtDto.setCode(district.getCode());
            districtDto.setFullName(district.getFullName());
            result.add(districtDto);
        }
        return result;
    }

    public List<WardDto> getWards(String districtCode) throws BizException {
        List<ProvinceDocument> provinceDocuments = provinceRepository.getAllProvinces(null, districtCode, null);
        if (provinceDocuments.isEmpty()) {
            throw new BizException("Invalid params");
        }
        ProvinceDocument.District districts = provinceDocuments.get(0).getDistricts().stream().filter(d -> d.getCode().equals(districtCode)).findFirst().orElseThrow(() -> new BizException("Invalid params"));
        List<ProvinceDocument.Ward> wards = districts.getWards();
        List<WardDto> result = new ArrayList<>();
        for (ProvinceDocument.Ward ward : wards) {
            WardDto wardDto = new WardDto();
            wardDto.setCode(ward.getCode());
            wardDto.setFullName(ward.getFullName());
            result.add(wardDto);
        }
        return result;
    }

//    public FullAddressDto getFullAddress(String wardCode) throws BizException {
//        List<ProvinceDocument> provinceDocuments = provinceRepository.getAllProvinces(null, null, wardCode);
//        if (provinceDocuments.isEmpty()) {
//            throw new BizException("Invalid params");
//        }
//        ProvinceDocument province = provinceDocuments.get(0);
//        ProvinceDocument.District district = province.getDistricts().stream().filter(d -> d.getWards().stream().anyMatch(w -> w.getCode().equals(wardCode))).findFirst().orElseThrow(() -> new BizException("Invalid params"));
//        ProvinceDocument.Ward ward = district.getWards().stream().filter(w -> w.getCode().equals(wardCode)).findFirst().orElseThrow(() -> new BizException("Invalid params"));
//        FullAddressDto result = new FullAddressDto();
//        String fullAddress = ward.getFullName() + ", " + district.getFullName() + ", " + province.getFullName();
//        result.setCode(ward.getCode());
//        result.setFullName(fullAddress);
//        return result;
//    }

//    public AddressDto getAddress(String provinceCode, String districtCode, String wardCode) throws BizException {
//        List<ProvinceDocument> provinceDocuments = provinceRepository.getAllProvinces(provinceCode, districtCode, wardCode);
//        if (provinceDocuments.isEmpty()) {
//            throw new BizException("Invalid params");
//        }
//        ProvinceDocument province = provinceDocuments.get(0);
//        ProvinceDocument.District district = province.getDistricts().stream().filter(d -> d.getCode().equals(districtCode)).findFirst().orElseThrow(() -> new BizException("Invalid params"));
//        ProvinceDocument.Ward ward = district.getWards().stream().filter(w -> w.getCode().equals(wardCode)).findFirst().orElseThrow(() -> new BizException("Invalid params"));
//        AddressDto result = new AddressDto();
//        result.setProvince(new ShippingAddressDocument.AddressDetail(province.getCode(), province.getFullName()));
//        result.setDistrict(new ShippingAddressDocument.AddressDetail(district.getCode(), district.getFullName()));
//        result.setWard(new ShippingAddressDocument.AddressDetail(ward.getCode(), ward.getFullName()));
//        return result;
//    }

}
