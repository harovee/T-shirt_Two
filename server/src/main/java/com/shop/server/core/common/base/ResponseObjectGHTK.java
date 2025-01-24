package com.shop.server.core.common.base;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
public class ResponseObjectGHTK<T> {

    private int code;

    private String message;

    private T data;

    public <V> ResponseObjectGHTK(T obj, String message, int code) {
        this.data = obj;
        this.message = message;
        this.code = code;
    }

}
