package box.bookstorebe.common;

public final class Const {
    public static final String BASE_PACKAGE = "box.bookstorebe";

    public enum EventType {
        BOOK_CREATE
    }

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
            "/api/v1/book-related-people/**",
            "/api/v1/entity/common/**"
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
        public static final String CREATED = "CREATED";
        public static final String CANCEL = "CANCEL";
        public static final String CONFIRM = "CONFIRM";
        public static final String SHIPPING = "SHIPPING";
        public static final String DONE = "DONE";
    }

    public static class BookStatus{
        public static final String NEW = "mới";
        public static final String GOOD = "đẹp";
        public static final String NORMAL = "khá";
        public static final String TB = "tạm";
    }
    public static final int SHIPPING_FEE = 25000;
    public static class CommonEntityType{
        public static final String TAG = "TAG";
        public static final String PUBLISHER = "PUBLISHER";

        public static boolean isValidType(String type) {
            return TAG.equals(type) || PUBLISHER.equals(type);
        }
    }

}
