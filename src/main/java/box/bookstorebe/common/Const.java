package box.bookstorebe.common;

import java.math.BigDecimal;

public final class Const {
    public static final String BASE_PACKAGE = "box.bookstorebe";

    public enum EventType {
        BOOK_CREATE
    }
    public enum AccountType{
        ADMIN, USER, STORE
    }
    public static final String[] AUTH_WHITELIST_PAYMENT = {
            "/api/v1/notifications"
    };
    public static final String[] AUTH_WHITELIST = {
            "/api/v1/auth/**",
            "/api/v1/collections/**",
            "/api/v1/books/**",
            "/api/v1/users/**",
            "/api/v1/orders/**",
            "/api/v1/book-reality/**",
            "/api/v1/categories/**",
            "/api/v1/book-stores/**",
            "/api/v1/collections/**",
            "/api/v1/book-search-requests/**",
            "/api/v1/people/**",
            "/api/v1/entity/common/**",
            "/api/v1/file/**",
            "/api/v1/notifications/**",
            "/api/v1/accounts/**",
            "/api/v1/customers/**",
            "/api/v1/inventories/**",
            "/api/v1/shipping/**",
            "/api/v1/carts/**",
            "/api/v1/provinces",
            "/api/v1/districts",
            "/api/v1/wards",
            "/api/v1/shipping-addresses/**",
            "/socket/**",
            "/api/v1/reports/**",
            "/api/v1/offline-orders/**",
    };



    public static class DateTime {
        public static final String TIME_ZONE = "Asia/Ho_Chi_Minh";
        public static final String SECOND_PRECISION_FORMAT = "yyyy/MM/dd-HH:mm:ss";
        public static final String DATETIME_FORMAT = "yyyy-MM-dd HH:mm:ss";
        public static final String DATE_FORMAT = "yyyy-MM-dd";
        public static final String DATE_FORMAT_1 = "dd/MM/yyyy";
        public static final String DATE_FORMAT_2 = "dd-MM-yyyy";
    }

    public static class ResultCode {
        public static final boolean SUCCESS = true;
        public static final boolean ERROR = false;
    }

    public enum BookSearchRequestStatus {
        NEW,
        IN_PROGRESS,
        COMPLETED,
        CANCELED
    }

    public enum BookRealityStatus {
        AVAILABLE,
        UNAVAILABLE
    }

    public enum BookRealityType {
        OLD,
        MEDIUM,
        NEW,
        GOOD
    }

    public enum BookDescriptionType {
        PUBLICATION_YEAR,
        SUMMARY,
        NUMBER_OF_PAGE,
        PUBLISHING_COMPANY
    }

    public enum BookRelatedPersonType {
        AUTHOR,
        EDITOR,
        TRANSLATOR,
        COVER_DRAWER
    }

    public enum BookImageType {
        COVER,
        DETAIL,
        DEMO
    }

    public static class OrderStatus {
        public static final String CREATED = "CREATED";
        public static final String CANCEL = "CANCEL";
        public static final String READY_TO_PACKAGE = "READY_TO_PACKAGE";
        public static final String READY_TO_SHIP = "READY_TO_SHIP";
        public static final String SHIPPING = "SHIPPING";
        public static final String DONE = "DONE";
    }

    public static class BookStatus{
        public static final String NEW = "mới";
        public static final String GOOD = "đẹp";
        public static final String NORMAL = "khá";
        public static final String TB = "tạm";
    }
    public static final BigDecimal SHIPPING_FEE = new BigDecimal(25000);
    public static class CommonEntityType{
        public static final String TAG = "TAG";
        public static final String PUBLISHER = "PUBLISHER";

        public static boolean isValidType(String type) {
            return TAG.equals(type) || PUBLISHER.equals(type);
        }
    }

    public static class SystemConfig {
        public static final String AUTHOR_NATIONALITY = "create_book.author.nationality";
        public static final String CREATE_BOOK_CATEGORY = "create_book.book.category";
        public static final String CREATE_BOOK_STORE = "create_book.book_store";
        public static final String AMOUNT_FREE_SHIP = "create_order.amount_free_ship";
        public static final String AMOUNT_SHIPPING_FEE = "create_order.amount_shipping_fee";
        public static final String PAYMENT_HARD_TOKEN = "payment.hard_token";
        public static final String CANCEL_ORDER_DURATION = "order.cancel_order_duration";

    }
    public static final BigDecimal AMOUNT_CAN_FREESHIP = new BigDecimal(500000);
    public static final String ADMIN_EMAIL = "hieusachhop@gmail.com";
    public static final String ADMIN_PASS = "123456";
    public static final String PICK_ADDRESS_CITY = "thành phố hà nội";
    public static final String PICK_ADDRESS_DISTRICT = "quận tây hồ";
    public enum SortDirection {
        ASC,
        DESC
    }
    public enum SettlementStatus{
        PENDING,
        DONE
    }
    public enum WebContentProperty {
        IMAGE,
        TEXT
    }
}
