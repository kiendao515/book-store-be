package box.bookstorebe.dto.ghtk;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.PropertyNamingStrategies;
import com.fasterxml.jackson.databind.annotation.JsonNaming;
import lombok.Data;

import java.util.List;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonNaming(PropertyNamingStrategies.SnakeCaseStrategy.class)
public class OrderDetail {
    private boolean success;
    private String message;
    private DataDTO data;

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DataDTO {
        private PackageDTO Package;
        private List<CreateLogDTO> CreateLog;
        private Object OrderCashAdvance;
        private List<Object> ImageFrage;
        @JsonProperty("PickLog")
        private List<PickLogDTO> PickLog;
        @JsonProperty("DeliverLog")
        private List<DeliverLogDTO> DeliverLog;
        private List<ReturnLogDTO> ReturnLog;
        private List<Object> AuditLog;
        private List<PrintLogDTO> PrintLog;
        private List<Object> CsLog;
        private List<OtherLogDTO> OtherLog;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PackageDTO {
        private String alias;
        private String customer_tel;
        private String customer_fullname;
        private String customer_last_address;
        private String customer_first_address;
        private String customer_province;
        private int customer_province_id;
        private int customer_district_id;
        private String customer_district;
        private int customer_ward_id;
        private String customer_ward;
        private String customer_street;
        private Integer customer_street_id;
        private int pick_money;
        private String pick_fullname;
        private String pick_first_address;
        private String pick_last_address;
        private String date_to_delay_pick;
        private int return_part_package;
        private int pick_address_id;
        private String pick_province;
        private int pick_province_id;
        private int pick_district_id;
        private String pick_district;
        private int pick_ward_id;
        private String pick_ward;
        private Integer pick_street_id;
        private String pick_street;
        private int weight;
        private String date_to_delay_deliver;
        private String id;
        private String created;
        private String approved_at;
        private String message;
        private int co_check_fee;
        private int return_fee;
        private int is_freeship;
        private int pre_paid_amount;
        private int store_fee;
        private int ship_money;
        private int insurance;
        private int package_status_id;
        private int deliver_work_shift;
        private int value;
        private String pick_tel;
        private int pick_work_shift;
        private String deliver_cart_id;
        private int transfer_station_id;
        private String transport;
        private int package_type;
        private String delay_deliver_reason_code;
        private int cur_station_id;
        private String client_id;
        private int tmp_picking_status;
        private int tmp_delivering_status;
        private String pick_cod_id;
        private int include_vat;
        private long packages_extends_id;
        private PickOptionDTO pick_option;
        private int is_xfast;
        private Integer change_address_fee;
        private int customer_hamlet_id;
        private int pick_hamlet_id;
        private int customer_corner_id;
        private String customer_corner_address;
        private String dest_station_name;
        private Object return_status;
        private String shop_id;
        private int used_coupon;
        private PickOptionDTO pick_option_v2;
        private long order;
        private int final_ship_fee;
        private int drop_off;
        private int tip_cod;
        private int sum_fee_materials;
        private Object fee_materials;
        private List<ProductDTO> products;
        private List<ProductDTO> product;
        private String customer_hamlet;
        private String pick_hamlet;
        private String pick_specific_id;
        private String pick_specific;
        private String customer_specific_id;
        private String customer_specific;
        private int ship_fee_only;
        private int re_delivery_fee;
        private int re_return_fee;
        private int change_return_add_fee;
        private int discount;
        private int pick_money_only;
        private String tmp_delivering_status_text;
        private String tmp_picking_status_text;
        private PackageValueDTO package_value;
        private List<Integer> tags;
        private List<Object> sub_tags;
        private String property;
        private String properties;
        private int type_id;
        private List<Object> details;
        private List<LogDTO> logs;
        private String deliver_cart_alias;
        private String package_status_id_text;
        private String delay_wait_fb_time;
        private String deliver_time_slot;
        private String pick_time_slot;
        private String master_shop_code;
        private String master_shop_name;
        private String shop_code;
        private int shop_type;
        private List<PackageAddressReturnDTO> package_addresses_return;
        private List<Object> pkg_tags;
        private int delivery_fee;
        private int final_service_fee;
        private int total_ship_money;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PickOptionDTO {
        private long id;
        private String package_id;
        private String pick_option;
        private Object station_id;
        private int has_insurance;
        private int include_vat;
        private int cost_id;
        private boolean is_fragile;
        private int is_xfast;
        private Object quick_audit;
        private String modified;
        private Object station_name;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ProductDTO {
        private long package_order;
        private String product_name;
        private int quantity;
        private long product_id;
        private List<String> image_urls;
        private int weight;
        private int width;
        private int height;
        private int length;
        private int price;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PackageValueDTO {
        private String short_expiry_date;
        private boolean count_down_expiry_date;
        private List<Object> sub_tags;
        private int deliver_waiting_minutes;
        private int total_box;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PackageAddressReturnDTO {
        private long id;
        private long package_order;
        private String type;
        private String name;
        private String email;
        private String tel;
        private String province;
        private int province_id;
        private String district;
        private int district_id;
        private String ward;
        private int ward_id;
        private String street;
        private int street_id;
        private String first_address;
        private String last_address;
        private double lat;
        private double lng;
        private int confirmed;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class LogDTO {
        private int id;
        private String package_id;
        private String shop_id;
        @JsonProperty("Package")
        private String packagee;
        private String action;
        private String old_value;
        private String new_value;
        private String created_user_id;
        private String created_username;
        private String desc;
        private String is_shop_viewed;
        private String created;
        private String modified;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class CreateLogDTO {
        private String desc;
        private String created;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PickLogDTO {
        private String created;
        private String desc;
        private long order;
        private Object pkg_id;
        private String action;
        private String image;
        private String pickImage;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DeliverLogDTO {
        private String created;
        private String desc;
        private long order;
        private Object pkg_id;
        private String action;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ReturnLogDTO {
        private String created;
        private String desc;
        private long order;
        private Object pkg_id;
        private String action;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PrintLogDTO {
        private long id;
        private long pkg_order;
        private String action;
        private String created_user_id;
        private String created_username;
        private String desc;
        private String created;
        private Object old_value;
        private Object new_value;
    }

    @Data
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class OtherLogDTO {
        private int id;
        private String package_id;
        private String shop_id;
        @JsonProperty("Package")
        private String packagee;
        private String action;
        private String old_value;
        private String new_value;
        private String created_user_id;
        private String created_username;
        private String desc;
        private String is_shop_viewed;
        private String created;
        private String modified;
    }

}
