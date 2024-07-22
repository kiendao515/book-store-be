package box.bookstorebe.common;

import java.util.List;

public final class Const {
    public static final String BASE_PACKAGE = "box.bookstorebe";

    public enum EventType {
        BOOK_CREATE
    }

    public static final String[] AUTH_WHITELIST = {
//            "/api/v1/auth/**",
//            "/api/v1/collections/**",
//            "/api/v1/orders/**"
            "**"
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

    public static class OrderStatus {
        public static final String CREATED =  "CREATED";
        public static final String CANCEL =  "CANCEL";
        public static final String CONFIRM =  "CONFIRM";
        public static final String SHIPPING =  "SHIPPING";
        public static final String DONE =  "DONE";
    }

}
