package box.bookstorebe.service.shipping;

import box.bookstorebe.client.CommonClient;
import box.bookstorebe.dto.ghtk.OrderDetail;
import box.bookstorebe.dto.ghtk.PickAddressDto;
import box.bookstorebe.model.order.ShippingFeeRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@AllArgsConstructor
@Service
@Slf4j
public class ShipService {
    private CommonClient commonClient;
    public BigDecimal calculateShippingFee(ShippingFeeRequest request){
        return commonClient.calculateShippingFee(request);
    }
    public OrderDetail getOrderDetail(String orderId){
        return commonClient.getOrderDetail(orderId);
    }
    public List<PickAddressDto.PickupData> getListPickAddress(){
        return commonClient.getPickAddress();
    }
}
