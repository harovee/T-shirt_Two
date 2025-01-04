package com.shop.server.infrastructure.security.exception;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
public class RedirectException extends RuntimeException {

    private String message;

}
