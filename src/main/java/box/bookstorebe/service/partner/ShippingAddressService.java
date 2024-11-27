package box.bookstorebe.service.partner;

import box.bookstorebe.common.Const;
import box.bookstorebe.configuration.security.RequestScope;
import box.bookstorebe.document.account.Role;

import box.bookstorebe.document.account.ShippingAddressDocument;
import box.bookstorebe.dto.common.AddressDto;
import box.bookstorebe.dto.customer.ShippingAddressDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.user.ShippingAddressModel;
import box.bookstorebe.repository.user.ShippingAddressRepository;
import box.bookstorebe.service.BaseService;
import box.bookstorebe.service.common.AddressService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import java.util.Objects;

@Service
@Slf4j
@AllArgsConstructor
public class ShippingAddressService extends BaseService {
    private final ShippingAddressRepository shippingAddressRepository;
    private final AddressService provinceService;

    public Page<ShippingAddressDto> getShippingAddress(String userId, Integer page, Integer size) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null || currentUser.getAccountId() == null) {
            throw new BizException("Invalid token");
        }
        String searchUserId = null;
        if (currentUser.getRole().equals(Role.ADMIN)) {
            if (userId != null) {
                searchUserId = userId;
            }

        } else {
            searchUserId = currentUser.getAccountId();
        }
        return shippingAddressRepository.getShippingAddresses(searchUserId, page, size);
    }


    public ShippingAddressDto getShippingAddressById(String id) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        ShippingAddressDocument shippingAddress = shippingAddressRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        if (!Objects.equals(shippingAddress.getUserId(), currentUser.getAccountId()) && currentUser.getRole() != Role.ADMIN) {
            throw new BizException("Invalid permission");
        }
        ShippingAddressDto shippingAddressDto = new ShippingAddressDto();
        shippingAddressDto.setId(shippingAddress.getId());
        shippingAddressDto.setFullName(shippingAddress.getFullName());
        shippingAddressDto.setPhoneNumber(shippingAddress.getPhoneNumber());
        shippingAddressDto.setProvince(shippingAddress.getProvince());
        shippingAddressDto.setDistrict(shippingAddress.getDistrict());
        shippingAddressDto.setWard(shippingAddress.getWard());
        shippingAddressDto.setStreet(shippingAddress.getStreet());
        shippingAddressDto.setDefault(shippingAddress.isDefault());
        return shippingAddressDto;
    }


    public void createShippingAddress(ShippingAddressModel shippingAddressModel) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        ShippingAddressDocument shippingAddress = new ShippingAddressDocument();
        shippingAddress.setUserId(currentUser.getAccountId());
        shippingAddress.setFullName(shippingAddressModel.getFullName());
        shippingAddress.setPhoneNumber(shippingAddressModel.getPhoneNumber());

        AddressDto addressDto = provinceService.getAddress(shippingAddressModel.getProvinceCode(), shippingAddressModel.getDistrictCode(), shippingAddressModel.getWardCode());
        shippingAddress.setProvince(addressDto.getProvince());
        shippingAddress.setDistrict(addressDto.getDistrict());
        shippingAddress.setWard(addressDto.getWard());
        shippingAddress.setStreet(shippingAddressModel.getStreet());
        shippingAddress.setDefault(shippingAddressModel.isDefault());
        shippingAddressRepository.save(shippingAddress);
    }

    public void updateShippingAddress(String id, ShippingAddressModel shippingAddressModel) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        ShippingAddressDocument shippingAddress = shippingAddressRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        if (!Objects.equals(shippingAddress.getUserId(), currentUser.getAccountId()) && currentUser.getRole() != Role.ADMIN) {
            throw new BizException("Invalid permission");
        }
        shippingAddress.setFullName(shippingAddressModel.getFullName());
        shippingAddress.setPhoneNumber(shippingAddressModel.getPhoneNumber());
        AddressDto addressDto = provinceService.getAddress(shippingAddressModel.getProvinceCode(), shippingAddressModel.getDistrictCode(), shippingAddressModel.getWardCode());
        shippingAddress.setProvince(addressDto.getProvince());
        shippingAddress.setDistrict(addressDto.getDistrict());
        shippingAddress.setWard(addressDto.getWard());
        shippingAddress.setStreet(shippingAddressModel.getStreet());
        shippingAddress.setDefault(shippingAddressModel.isDefault());
        shippingAddressRepository.save(shippingAddress);

    }

    public void deleteShippingAddress(String id) throws BizException {
        RequestScope currentUser = this.getCurrentUserInfo();
        if (currentUser == null) {
            throw new BizException("Invalid token");
        }
        ShippingAddressDocument shippingAddress = shippingAddressRepository.findById(id).orElseThrow(() -> new BizException("Invalid id"));
        if (!Objects.equals(shippingAddress.getUserId(), currentUser.getAccountId())) {
            throw new BizException("Invalid permission");
        }
        shippingAddressRepository.delete(shippingAddress);
    }
}
