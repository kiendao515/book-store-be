package box.bookstorebe.dto.common;


import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
public class BaseResponse<T> {
    private boolean success;
    private T data;
    private String message;

    public BaseResponse(boolean success, T data) {
        super();
        this.success = success;
        this.data = data;
    }

    public BaseResponse(boolean success, String message) {
        super();
        this.success = success;
        this.message = message;
    }

    public BaseResponse(boolean success, T data, String message) {
        super();
        this.success = success;
        this.data = data;
        this.message = message;
    }
}