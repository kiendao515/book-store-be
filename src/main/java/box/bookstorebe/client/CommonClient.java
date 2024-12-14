package box.bookstorebe.client;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.ghtk.GhtkDto;
import box.bookstorebe.dto.ghtk.GhtkOrderDto;
import box.bookstorebe.dto.ghtk.OrderDetail;
import box.bookstorebe.dto.ghtk.PickAddressDto;
import box.bookstorebe.model.order.GhtkOrderRequest;
import box.bookstorebe.model.order.ShippingFeeRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.http.converter.ByteArrayHttpMessageConverter;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
public class CommonClient {
    @Value("${app.client.url}")
    private String clientUrl;
    @Value("${app.domain.url}")
    private String serverUrl;
    @Value("${app.ghtk.url}")
    private String ghtkUrl;
    @Value("${app.ghtk.token}")
    private String ghtkToken;
    @Value("${app.ghtk.web}")
    private String webGhtkUrl;
    @Value("${app.ghtk.webToken}")
    private String webGhtkToken;
    @Value("${app.ghtk.urlProd}")
    private String ghtkUrlProd;
    @Value("${app.ghtk.tokenProd}")
    private String ghtkTokenProd;

    public BigDecimal calculateShippingFee(ShippingFeeRequest request) {
        String url = ghtkUrlProd + "/services/shipment/fee?" +
                "address=" + request.getAddress() +
                "&province=" + request.getProvince() +
                "&district=" + request.getDistrict() +
                "&pick_province=thành phố hà nội" +
                "&pick_district=quận tây hồ" +
                "&weight=" + request.getWeight() +
                "&value=" + request.getValue() +
                "&deliver_option=" + request.getDeliverOption();

        RestTemplate restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token", ghtkTokenProd);
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            log.info(response.getBody());
            ObjectMapper objectMapper = new ObjectMapper();
            GhtkDto ghtkDto = objectMapper.readValue(response.getBody(), GhtkDto.class);
            return BigDecimal.valueOf(ghtkDto.getFee().getFee());
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return new BigDecimal(25000);
    }

    public OrderDetail getOrderDetail(String id) {
        String url = webGhtkUrl + "/api/v1/package/package-detail?alias=" + id;
        RestTemplate restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Authorization", "Bearer " + webGhtkToken);
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            log.info(response.getBody());
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(response.getBody(), OrderDetail.class);
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }

    public List<PickAddressDto.PickupData> getPickAddress() {
        String url = ghtkUrl + "/services/shipment/list_pick_add";
        RestTemplate restTemplate = new RestTemplate();
        var headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.add("Token", ghtkToken);
        try {
            HttpEntity<Map<String, Object>> requestEntity = new HttpEntity<>(null, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.GET, requestEntity, String.class);
            log.info(response.getBody());
            ObjectMapper objectMapper = new ObjectMapper();
            PickAddressDto ghtkDto = objectMapper.readValue(response.getBody(), PickAddressDto.class);
            return Arrays.stream(ghtkDto.getData()).toList();
        } catch (Exception e) {
            log.info(e.getMessage());
        }
        return null;
    }

    public GhtkOrderDto.OrderResult createOrder(GhtkOrderRequest ghtkOrderRequest) {
        try {
            String url = ghtkUrl + "/services/shipment/order";
            RestTemplate restTemplate = new RestTemplate();
            var headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.add("Token", ghtkToken);
            HttpEntity<GhtkOrderRequest> httpRequest = new HttpEntity<>(ghtkOrderRequest, headers);
            ResponseEntity<String> response = restTemplate.exchange(url, HttpMethod.POST, httpRequest, String.class);
            log.info(response.toString());
            ObjectMapper objectMapper = new ObjectMapper();
            GhtkOrderDto ghtkDto = objectMapper.readValue(response.getBody(), GhtkOrderDto.class);
            return ghtkDto.getOrder();
        } catch (Exception e) {
            log.info(e.getMessage());
            return null;
        }

    }

    public byte[] printOrder(String label) {
        try {
            String url = ghtkUrl + "/services/label/" + label;
            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getMessageConverters().add(new ByteArrayHttpMessageConverter()); // Thêm ByteArrayHttpMessageConverter
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_PDF);
            headers.add("Token", ghtkToken);
            HttpEntity<Void> httpRequest = new HttpEntity<>(null, headers);
            ResponseEntity<byte[]> response = restTemplate.exchange(url, HttpMethod.GET, httpRequest, byte[].class);
            log.info(response.toString());
            log.info("Response Status: " + response.getStatusCode());
            if (response.getStatusCode() == HttpStatus.OK) {
                return response.getBody();
            }

        } catch (Exception e) {
            log.error("Error occurred: " + e.getMessage(), e);
        }
        return null;
    }

}
