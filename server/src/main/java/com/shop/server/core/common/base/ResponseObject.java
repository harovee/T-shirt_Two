package com.shop.server.core.common.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.http.HttpStatus;

import java.time.Instant;

@Getter
@Setter
@ToString
@NoArgsConstructor
public class ResponseObject<T> {

    private boolean isSuccess = false;

    private HttpStatus status;

    private T data;

    private String message;

    private Instant timestamp = Instant.now();

    public ResponseObject(T obj, HttpStatus status, String message) {
        processResponseObject(obj);
        this.status = status;
        this.message = message;
    }

    public ResponseObject(T obj) {
        processResponseObject(obj);
    }


    public void processResponseObject(T obj) {
        if (obj != null) {
            this.isSuccess = true;
            this.data = obj;
        }
    }

    public ResponseObject<T> success(T obj, String message) {
        processResponseObject(obj);
        this.status = HttpStatus.OK;
        this.message = message;
        return this;
    }

    public static <T> ResponseObject<T> success(String message) {
        ResponseObject<T> responseObject = new ResponseObject<>();
        responseObject.setSuccess(true);
        responseObject.setStatus(HttpStatus.OK);
        responseObject.setData(null);
        responseObject.setMessage(message);
        return responseObject;
    }

    public ResponseObject<T> error(HttpStatus status, String message) {
        this.status = status;
        this.message = message;
        return this;
    }

    public static <T> ResponseObject<T> successForward(T obj, String message) {
        ResponseObject<T> responseObject = new ResponseObject<>();
        responseObject.setSuccess(true);
        responseObject.setStatus(HttpStatus.OK);
        responseObject.setData(obj);
        responseObject.setMessage(message);
        return responseObject;
    }

    public static <T> ResponseObject<T> errorForward(HttpStatus status, String message) {
        ResponseObject<T> responseObject = new ResponseObject<>();
        responseObject.setSuccess(false);
        responseObject.setStatus(status);
        responseObject.setMessage(message);
        return responseObject;
    }

}
