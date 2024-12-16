package box.bookstorebe.model.order;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GhtkOrderRequest {
    private List<Product> products;
    private Order order;
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Product {
        private String name;
        private double weight;
        private int quantity;
        private int productCode;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Order {
        private String id;
        private String pick_name;
        private String pick_address;
        private String pick_province;
        private String pick_district;
        private String pick_ward;
        private String pick_tel;
        private String tel;
        private String name;
        private String address;
        private String province;
        private String district;
        private String ward;
        private String hamlet;
        private String is_freeship;
        private String pick_date;
        private BigDecimal pick_money;
        private String note;
        private BigDecimal value;
        private String transport;
        private String pick_option;
//        private String deliver_option;
//        private int pick_session;
    }
}


