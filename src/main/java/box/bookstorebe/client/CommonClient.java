package box.bookstorebe.client;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.ghtk.GhtkDto;
import box.bookstorebe.model.order.ShippingFeeRequest;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.math.BigDecimal;
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

    public BigDecimal calculateShippingFee(ShippingFeeRequest request) {
        String url = ghtkUrl + "/services/shipment/fee?" +
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
        headers.add("Token", ghtkToken);
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
}
