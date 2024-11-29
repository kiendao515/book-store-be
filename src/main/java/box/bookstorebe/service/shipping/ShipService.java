package box.bookstorebe.service.shipping;

import box.bookstorebe.client.CommonClient;
import box.bookstorebe.document.order.OrderDocument;
import box.bookstorebe.dto.ghtk.GhtkOrderDto;
import box.bookstorebe.dto.ghtk.OrderDetail;
import box.bookstorebe.dto.ghtk.PickAddressDto;
import box.bookstorebe.exception.BizException;
import box.bookstorebe.model.order.CreateOrder;
import box.bookstorebe.model.order.GhtkOrderRequest;
import box.bookstorebe.model.order.ShippingFeeRequest;
import box.bookstorebe.repository.order.OrderRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@Service
@Slf4j
public class ShipService {
    private CommonClient commonClient;
    private final OrderRepository orderRepository;
    public BigDecimal calculateShippingFee(ShippingFeeRequest request){
        return commonClient.calculateShippingFee(request);
    }
    public OrderDetail getOrderDetail(String orderId){
        return commonClient.getOrderDetail(orderId);
    }
    public List<PickAddressDto.PickupData> getListPickAddress(){
        return commonClient.getPickAddress();
    }
    public static Map<String, String> parseAddress(String inputAddress) throws BizException {
        Map<String, String> addressComponents = new HashMap<>();
        String[] parts = inputAddress.split(",");

        if (parts.length < 4) {
            throw new BizException("Địa chỉ không đầy đủ thông tin.");
        }

        String streetAndNumber = parts[0].trim();
        String ward = parts[1].trim();
        String district = parts[2].trim();
        String city = parts[3].trim();
        addressComponents.put("street", streetAndNumber);
        addressComponents.put("ward", ward);
        addressComponents.put("district", district);
        addressComponents.put("city", city);
        return addressComponents;
    }
    public GhtkOrderDto.OrderResult createGhtkOrder(CreateOrder orderDto) throws BizException {
        OrderDocument orderDocument = orderRepository.findByOrderCode(orderDto.getOrderCode());
        GhtkOrderRequest ghtkOrderRequest = new GhtkOrderRequest();
        GhtkOrderRequest.Order order = new GhtkOrderRequest.Order();
        order.setId(orderDocument.getOrderCode());
        order.setPickName(orderDto.getPickName());
        order.setPickAddress(orderDto.getPickAddress());
        Map<String,String > pickAddress = parseAddress(orderDto.getPickAddress());

        // thông tin lấy hàng
        order.setPickAddress(pickAddress.get("street"));
        order.setPickProvince(pickAddress.get("city"));
        order.setPickDistrict(pickAddress.get("district"));
        order.setPickWard(pickAddress.get("ward"));
        order.setPickTel(orderDto.getPickTel());

        // thông tin nhận hàng
        order.setTel(orderDocument.getReceiverName());
        order.setName(orderDocument.getReceiverPhone());
        order.setAddress(orderDocument.getStreet());
        order.setProvince(orderDocument.getProvince().getFullName());
        order.setDistrict(orderDocument.getDistrict().getFullName());
        order.setWard(orderDocument.getWard().getFullName());
        order.setHamlet("Khác");
        order.setIsFreeship("1");
        order.setPickDate(order.getPickDate());
        if(!orderDocument.isPaymentType()){
            order.setPickMoney(orderDocument.getTotalAmount());
        }else {
            order.setPickMoney(BigDecimal.ZERO);
        }
        order.setNote(orderDto.getNote());
        order.setValue(orderDocument.getTotalAmount());
        ghtkOrderRequest.setOrder(order);

        List<GhtkOrderRequest.Product> product = new ArrayList<>();
        GhtkOrderRequest.Product product1 = new GhtkOrderRequest.Product();
        product1.setName("sách");
        product1.setWeight(orderDto.getWeight());
        product.add(product1);
        ghtkOrderRequest.setProducts(product);

        return commonClient.createOrder(ghtkOrderRequest);
    }
}
