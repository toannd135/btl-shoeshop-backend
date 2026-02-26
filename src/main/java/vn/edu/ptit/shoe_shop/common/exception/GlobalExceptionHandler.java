package vn.edu.ptit.shoe_shop.common.exception;

import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;
import org.springframework.web.multipart.MaxUploadSizeExceededException;
import org.springframework.web.servlet.NoHandlerFoundException;
import vn.edu.ptit.shoe_shop.dto.response.ApiResponse;
import java.util.List;
import java.util.stream.Collectors;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(IdInvalidException.class)
    public ResponseEntity<ApiResponse<Object>> handleIdInvalidException(IdInvalidException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Invalid ID Exception");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        BindingResult bindingResult = e.getBindingResult();
        List<FieldError> fieldErrors = bindingResult.getFieldErrors();

        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Validation Failed");

        List<String> errors = fieldErrors
                .stream()
                .map(FieldError::getDefaultMessage)
                .collect(Collectors.toList());
        res.setMessage(errors.size() > 1 ? errors : errors.get(0));

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(UsernameNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleUsernameNotFoundException(UsernameNotFoundException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("User Not Found");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ApiResponse<Object>> handleBadCredentialsException(BadCredentialsException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.UNAUTHORIZED.value());
        res.setError("Authentication Failed");
        res.setMessage(e.getMessage() != null ? e.getMessage() : "Invalid credentials");
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(res);
    }

    @ExceptionHandler(IllegalStateException.class)
    public ResponseEntity<ApiResponse<Object>> handleIllegalStateException(IllegalStateException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Invalid State");
        String message = e.getMessage();
        String customMessage;
        if (message.contains("already exists") || message.contains("already in use")) {
            customMessage = "Resource is already in use or exists.";
        } else if (message.contains("cannot be modified") || message.contains("cannot be changed")) {
            customMessage = "Resource cannot be modified in its current state.";
        } else if (message.contains("invalid state") || message.contains("not allowed")) {
            customMessage = "Operation not allowed in current state.";
        } else {
            customMessage = e.getMessage();
        }
        res.setMessage(customMessage);
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(ResourceNotFoundException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Resource Not Found");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }
    @ExceptionHandler(NotFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleResourceNotFoundException(NotFoundException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Entity Not Found");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(DuplicateResourceException.class)
    public ResponseEntity<ApiResponse<Object>> handleDuplicateResourceException(DuplicateResourceException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.CONFLICT.value());
        res.setError("Duplicate Resource");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ApiResponse<Object>> handleBusinessException(BusinessException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Business Rule Violation");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }


    @ExceptionHandler(LockedException.class)
    public ResponseEntity<ApiResponse<Object>> handleLockedException(LockedException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.LOCKED.value());
        res.setError("Account Locked");
        res.setMessage("Your account has been locked. Please contact administrator.");
        return ResponseEntity.status(HttpStatus.LOCKED).body(res);
    }

    @ExceptionHandler(AccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleAccessDeniedException(Exception e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("Access Denied");
        res.setMessage("You don't have permission to access this resource");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    @ExceptionHandler(MaxUploadSizeExceededException.class)
    public ResponseEntity<ApiResponse<Object>> handleMaxUploadSizeExceededException(MaxUploadSizeExceededException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.PAYLOAD_TOO_LARGE.value());
        res.setError("File Too Large");
        res.setMessage("Maximum upload size exceeded");
        return ResponseEntity.status(HttpStatus.PAYLOAD_TOO_LARGE).body(res);
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<ApiResponse<Object>> handleMissingServletRequestParameterException(
            MissingServletRequestParameterException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Missing Request Parameter");
        res.setMessage("Required parameter '" + e.getParameterName() + "' is missing");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(MethodArgumentTypeMismatchException.class)
    public ResponseEntity<ApiResponse<Object>> handleMethodArgumentTypeMismatchException(
            MethodArgumentTypeMismatchException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Type Mismatch");
        res.setMessage("Invalid value for parameter '" + e.getName() + "'");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(HttpMessageNotReadableException.class)
    public ResponseEntity<ApiResponse<Object>> handleHttpMessageNotReadableException(HttpMessageNotReadableException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.BAD_REQUEST.value());
        res.setError("Invalid Request Body");
        res.setMessage("Malformed JSON request or invalid data format");
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(res);
    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<ApiResponse<Object>> handleNoHandlerFoundException(NoHandlerFoundException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.NOT_FOUND.value());
        res.setError("Endpoint Not Found");
        res.setMessage("The requested endpoint does not exist: " + e.getRequestURL());
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(res);
    }

    @ExceptionHandler(NioAccessDeniedException.class)
    public ResponseEntity<ApiResponse<Object>> handleNioAccessDeniedException(NioAccessDeniedException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.FORBIDDEN.value());
        res.setError("File Access Denied");
        res.setMessage("Access to file system denied");
        return ResponseEntity.status(HttpStatus.FORBIDDEN).body(res);
    }

    @ExceptionHandler(DataIntegrityViolationException.class)
    public ResponseEntity<ApiResponse<Object>> handleDataIntegrityViolationException(DataIntegrityViolationException e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.CONFLICT.value());
        res.setError("Database Constraint Violation");
        res.setMessage(e.getMessage());
        return ResponseEntity.status(HttpStatus.CONFLICT).body(res);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ApiResponse<Object>> handleGenericException(Exception e) {
        ApiResponse<Object> res = new ApiResponse<>();
        res.setStatusCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        res.setError("Internal Server Error");
        if ("prod".equals(System.getProperty("env"))) {
            res.setMessage("An unexpected error occurred. Please try again later.");
        } else {
            res.setMessage(e.getMessage());
        }
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(res);
    }
}