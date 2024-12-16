package box.bookstorebe.service.shipping;

import box.bookstorebe.client.CommonClient;
import box.bookstorebe.common.Const;
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
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@AllArgsConstructor
@Service
@Slf4j
public class ShipService {
    private CommonClient commonClient;
    private final OrderRepository orderRepository;
    private final ExecutorService executorService = Executors.newFixedThreadPool(10);
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
        String[] addressParts = inputAddress.split(",");
        if (addressParts.length < 4) {
            throw new BizException("Địa chỉ không đầy đủ thông tin.");
        }
        String streetAndNumber = addressParts[1].trim();
        String ward = addressParts[2].trim();
        String district = addressParts[3].trim();
        String city = addressParts[4].trim();
        addressComponents.put("street", streetAndNumber);
        addressComponents.put("ward", ward);
        addressComponents.put("district", district);
        addressComponents.put("city", city);

        return addressComponents;
    }
    public List<GhtkOrderDto.OrderResult> createGhtkOrders(List<CreateOrder> orders) throws BizException {
        List<GhtkOrderDto.OrderResult> orderResults = new ArrayList<>();

        List<Future<GhtkOrderDto.OrderResult>> futures = new ArrayList<>();

        for (CreateOrder orderDto : orders) {
            futures.add(executorService.submit(() -> {
                return createGhtkOrder(orderDto);
            }));
        }

        for (Future<GhtkOrderDto.OrderResult> future : futures) {
            try {
                GhtkOrderDto.OrderResult orderResult = future.get();
                if (orderResult != null) {
                    orderResults.add(orderResult);
                }
            } catch (InterruptedException | ExecutionException e) {
                log.info(e.getMessage());
                throw new BizException("Lỗi trong khi xử lý đơn hàng");
            }
        }

        return orderResults;
    }

    private GhtkOrderDto.OrderResult createGhtkOrder(CreateOrder orderDto) throws BizException {
        // Logic xử lý cho một đơn hàng, như đã có trong mã của bạn
        OrderDocument orderDocument = orderRepository.findByOrderCode(orderDto.getOrderCode());
        GhtkOrderRequest ghtkOrderRequest = new GhtkOrderRequest();
        GhtkOrderRequest.Order order = new GhtkOrderRequest.Order();
        order.setId(orderDocument.getOrderCode());
        order.setPick_address(orderDto.getPickAddress());
        List<PickAddressDto.PickupData> pickupData = commonClient.getPickAddress();
        Map<String,String > pickAddress = parseAddress(pickupData.get(0).getAddress());

        // thông tin lấy hàng
        order.setPick_name(pickupData.get(0).getPickName());
        order.setPick_address(pickAddress.get("street"));
        order.setPick_province(pickAddress.get("city"));
        order.setPick_district(pickAddress.get("district"));
        order.setPick_ward(pickAddress.get("ward"));
        order.setPick_tel(pickupData.get(0).getPickTel());

        // thông tin nhận hàng
        order.setTel(orderDto.getCustomerPhone());
        order.setName(orderDto.getCustomerName());
        order.setAddress(orderDocument.getStreet());
        order.setProvince(orderDocument.getProvince().getFullName());
        order.setDistrict(orderDocument.getDistrict().getFullName());
        order.setWard(orderDocument.getWard().getFullName());
        order.setHamlet("Khác");
        order.setIs_freeship("1");
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        order.setPick_date(ZonedDateTime.now().format(formatter));
        order.setPick_option("cod");
        order.setTransport("road");
        if(!orderDocument.isPaymentType()){
            order.setPick_money(orderDocument.getTotalAmount());
        } else {
            order.setPick_money(BigDecimal.ZERO);
        }
        order.setNote(orderDto.getNote());
        order.setValue(orderDocument.getTotalAmount());
        ghtkOrderRequest.setOrder(order);

        List<GhtkOrderRequest.Product> product = new ArrayList<>();
        GhtkOrderRequest.Product product1 = new GhtkOrderRequest.Product();
        product1.setName("sách");
        product1.setWeight(Float.parseFloat(orderDto.getWeight()));
        product.add(product1);
        ghtkOrderRequest.setProducts(product);

        GhtkOrderDto.OrderResult orderResult = commonClient.createOrder(ghtkOrderRequest);
        if(orderResult != null){
            orderDocument.setStatus(Const.OrderStatus.READY_TO_SHIP);
            orderDocument.setWeight(Float.parseFloat(orderDto.getWeight()));
            orderDocument.setShippingCode(orderResult.getLabel());
            orderRepository.save(orderDocument);
        }
        return orderResult;
    }

    public byte[] printOrder(String label){
        return commonClient.printOrder(label);
    }
}
