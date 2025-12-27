package com.danilojbs.couponapi.domain.exception;

public class CouponNotFoundException extends RuntimeException {
    // Exception responsável para erro de cupom não encontrado (inexistente)
    public CouponNotFoundException(String id) {
        super("Coupon not found with id: " + id);
    }
}
