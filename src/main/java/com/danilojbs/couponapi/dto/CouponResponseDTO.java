package com.danilojbs.couponapi.dto;

import com.danilojbs.couponapi.domain.Coupon;

import java.math.BigDecimal;
import java.time.Instant;

// Utilizado record para dados imutáveis
public record CouponResponseDTO(
        String id,
        String code,
        String description,
        BigDecimal discountValue,
        Instant expirationDate,
        Boolean published
) {

    /*
    * Função Factory responsável pela conversão do Cupom para DTO
    * Evita expor todos os dados e filtrar quais campos serão enviados ao usuário
    */
    public static CouponResponseDTO from(Coupon coupon) {
        return new CouponResponseDTO(
                coupon.getId(),
                coupon.getCode(),
                coupon.getDescription(),
                coupon.getDiscountValue(),
                coupon.getExpirationDate(),
                coupon.getPublished()
        );
    }
}
