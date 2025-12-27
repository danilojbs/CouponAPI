package com.danilojbs.couponapi.domain.exception;

public class CouponBusinessException extends RuntimeException {
    // Exception responsável por erros da regra de negócio
    public CouponBusinessException(String message) {
        super(message);
    }
}
