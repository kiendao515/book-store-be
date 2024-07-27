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

    public enum BookRealityType {
        OLD,
        MEDIUM,
        NEW
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
        public static final String CREATED =  "CREATED";
        public static final String CANCEL =  "CANCEL";
        public static final String CONFIRM =  "CONFIRM";
        public static final String SHIPPING =  "SHIPPING";
        public static final String DONE =  "DONE";
    }

    public static class BookStatus{
        public static final String NEW = "tốt";
        public static final String NORMAL = "tạm";
        public static final String TB = "trung bình";
    }

}
