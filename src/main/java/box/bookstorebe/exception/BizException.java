package box.bookstorebe.exception;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.io.Serial;

public class BizException extends Exception {
    @Serial
    private static final long serialVersionUID = 1L;

    public BizException(String message) {
        super(message);
    }

    public BizException(String errorMessage, Throwable error) {
        super(errorMessage, error);
    }

}
