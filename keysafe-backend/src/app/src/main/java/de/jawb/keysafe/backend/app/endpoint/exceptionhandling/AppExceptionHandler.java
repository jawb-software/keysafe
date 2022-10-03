package de.jawb.keysafe.backend.app.endpoint.exceptionhandling;

import de.jawb.keysafe.backend.api.ApiErrorInfo;
import de.jawb.keysafe.backend.core.base.AppException;
import de.jawb.keysafe.backend.core.base.DataNotFoundException;
import de.jawb.keysafe.backend.core.service.exceptions.BackupUnchangedException;
import de.jawb.tools.exception.ExceptionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.util.List;
import java.util.stream.Collectors;

@ControllerAdvice
public class AppExceptionHandler {

    private static final Logger logger = LoggerFactory.getLogger(AppExceptionHandler.class);

    private final ResponseEntityExceptionHandler SPRING_DEFAULT = new ResponseEntityExceptionHandler(){};

    @ExceptionHandler(AppException.class)
    public ResponseEntity<?> handleAppException(AppException e){
        logger.error("handling app exception", e);

        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;

        if(e instanceof DataNotFoundException){
            code = HttpStatus.NOT_FOUND;
        } else if (e instanceof BackupUnchangedException){
            code = HttpStatus.NOT_MODIFIED;
        }

        return createResponse(code, e);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleUnknownException(Exception e, WebRequest webRequest){
        if(e instanceof BindException){
            logger.error("handling bind exception: {}", errorDetailsFromException(e));
        } else {
            logger.error("handling unknown exception", e);
        }

        HttpStatus code = HttpStatus.INTERNAL_SERVER_ERROR;
        try {
            ResponseEntity<Object> response = SPRING_DEFAULT.handleException(e, webRequest);
            if(response != null){
                code = response.getStatusCode();
            }
        } catch (Exception ex){
            logger.error("Error", e);
        }

        return createResponse(code, e);
    }

    private ResponseEntity<ApiErrorInfo> createResponse(HttpStatus code, Exception e){

        Object details         = errorDetailsFromException(e);
        ApiErrorInfo errorInfo = new ApiErrorInfo(code.value(), code.getReasonPhrase(), details);

        logger.info("response code: {}", code);
        return ResponseEntity.status(code).body(errorInfo);
    }

    private Object errorDetailsFromException(Exception e){

        if(e instanceof BindException bindEx){
            BindingResult br = bindEx.getBindingResult();
            List<FieldError> errors = br.getFieldErrors();
            return errors.stream().map(fe -> {
                return fe.getField() + ": " + fe.getDefaultMessage();
            }).collect(Collectors.toList());
        }

        return List.of(ExceptionUtil.getErrorMessage(e));
    }

}
