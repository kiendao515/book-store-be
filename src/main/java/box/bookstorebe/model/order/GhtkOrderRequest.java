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
        private String pickName;
        private String pickAddress;
        private String pickProvince;
        private String pickDistrict;
        private String pickWard;
        private String pickTel;
        private String tel;
        private String name;
        private String address;
        private String province;
        private String district;
        private String ward;
        private String hamlet;
        private String isFreeship;
        private String pickDate;
        private BigDecimal pickMoney;
        private String note;
        private BigDecimal value;
        private String transport;
        private String pickOption;
        private String deliverOption;
        private int pickSession;
    }
}


