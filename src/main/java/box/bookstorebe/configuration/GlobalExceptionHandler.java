package box.bookstorebe.configuration;

import box.bookstorebe.common.Const;
import box.bookstorebe.dto.common.BaseResponse;
import box.bookstorebe.exception.BizException;
import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.ConstraintViolation;
import jakarta.validation.ConstraintViolationException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.util.ContentCachingRequestWrapper;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

@Order(Ordered.HIGHEST_PRECEDENCE)
@RestControllerAdvice
public class GlobalExceptionHandler {
    private static final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    private ResponseEntity<Object> buildResponseEntity(BaseResponse<Object> response, HttpStatus status) {
        return new ResponseEntity<>(response, status);
    }

    @ResponseBody
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleAllException(Exception e, HttpServletRequest request) {

        StringBuilder sb = new StringBuilder(e.getClass().toString());
        sb.append("URI: ").append(request.getRequestURI()).append(System.lineSeparator());
        sb.append("ERROR: ").append(e.getLocalizedMessage()).append(System.lineSeparator());

        try {
            ContentCachingRequestWrapper requestWrapper = (ContentCachingRequestWrapper) request;
            String requestWrapperbody = new String(requestWrapper.getContentAsByteArray(), StandardCharsets.UTF_8);
            sb.append("Body: ").append(requestWrapperbody).append(System.lineSeparator());
        } catch (Exception ignored) {
        }

        sb.append("StackTrace: ").append(System.lineSeparator());


        StackTraceElement[] elem = e.getStackTrace();
        for (StackTraceElement stackTraceElement : elem) {
            sb.append(stackTraceElement.toString()).append(System.lineSeparator());
        }

        logger.error("handleAllException occurred : {}", sb);

        BaseResponse<Object> response = new BaseResponse<>();
        response.setResult(Const.ResultCode.ERROR);
        response.setReason(e.getMessage());
        return buildResponseEntity(response, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    //Request param error
    @ResponseBody
    @ExceptionHandler(MethodArgumentNotValidException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        final List<FieldError> fieldErrors = ex.getBindingResult().getFieldErrors();
        Map<String, Set<String>> errorsMap = fieldErrors.stream().collect(Collectors.groupingBy(FieldError::getField,
                Collectors.mapping(FieldError::getDefaultMessage, Collectors.toSet())));

        BaseResponse<Object> response = new BaseResponse<>();
        response.setResult(Const.ResultCode.ERROR);
        response.setReason("Validation error");
        response.setData(errorsMap.isEmpty() ? ex : errorsMap);
        return buildResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    //Invalid request body
    @ResponseBody
    @ExceptionHandler(ConstraintViolationException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValidException(ConstraintViolationException ex) {
        Set<ConstraintViolation<?>> fieldErrors = ex.getConstraintViolations();
        StringBuilder message = new StringBuilder();
        for (ConstraintViolation<?> constraintViolation : fieldErrors) {
            message.append(constraintViolation.getMessage()).append(';');
        }
        BaseResponse<Object> response = new BaseResponse<>();
        response.setResult(Const.ResultCode.ERROR);
        response.setReason("Validation error");
        response.setData(message.toString());
        return buildResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    //Business in code
    @ResponseBody
    @ExceptionHandler(BizException.class)
    protected ResponseEntity<Object> handleMethodArgumentNotValid(BizException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setResult(Const.ResultCode.ERROR);
        response.setReason(ex.getMessage());
        return buildResponseEntity(response, HttpStatus.OK);
    }

    @ResponseBody
    @ExceptionHandler(MissingServletRequestParameterException.class)
    protected ResponseEntity<Object> handleMissingServletRequestParameterException(MissingServletRequestParameterException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setResult(Const.ResultCode.ERROR);
        response.setReason("Thiếu " + ex.getParameterName());
        return buildResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    //Custom validation error
    @ResponseBody
    @ExceptionHandler(org.springframework.http.converter.HttpMessageNotReadableException.class)
    protected ResponseEntity<Object> handleHttpMessageNotReadableException(org.springframework.http.converter.HttpMessageNotReadableException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        Throwable cause = ex.getRootCause();
        if (cause instanceof InvalidFormatException) {
            response.setResult(Const.ResultCode.ERROR);
            response.setReason(((InvalidFormatException) cause).getOriginalMessage());
            return buildResponseEntity(response, HttpStatus.BAD_REQUEST);
        }
        response.setResult(Const.ResultCode.ERROR);
        response.setReason(ex.getMessage());
        return buildResponseEntity(response, HttpStatus.BAD_REQUEST);
    }

    @ResponseBody
    @ExceptionHandler(org.springframework.security.access.AccessDeniedException.class)
    protected ResponseEntity<Object> handleAccessDeniedException(org.springframework.security.access.AccessDeniedException ex) {
        BaseResponse<Object> response = new BaseResponse<>();
        response.setResult(Const.ResultCode.ERROR);
        response.setReason(ex.getMessage());
        return buildResponseEntity(response, HttpStatus.FORBIDDEN);
    }


}
