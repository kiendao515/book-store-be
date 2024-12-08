package box.bookstorebe.dto.common;


import box.bookstorebe.dto.report.OrderReportDto;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class BaseResponse<T> {
    private boolean result;
    private T data;
    private String reason;

    public BaseResponse(boolean success, T data) {
        super();
        this.result = success;
        this.data = data;
    }

    public BaseResponse(boolean success, String message) {
        super();
        this.result = success;
        this.reason = message;
    }

    public BaseResponse(boolean success, T data, String message) {
        super();
        this.result = success;
        this.data = data;
        this.reason = message;
    }
}