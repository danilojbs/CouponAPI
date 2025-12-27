package com.danilojbs.couponapi.domain;

import com.danilojbs.couponapi.domain.exception.CouponBusinessException;
import jakarta.persistence.*;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

@Entity
@Table(name = "coupons")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
@Getter
public class Coupon {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private String id;

    @Column(unique = true, nullable = false, length = 6)
    private String code;

    @Column(nullable = false)
    private String description;

    @Column(nullable = false)
    private BigDecimal discountValue;

    @Column(nullable = false)
    private Instant expirationDate;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    @Setter
    private CouponStatus status;

    @Column(nullable = false)
    @Setter
    private Boolean published;

    @Setter
    private Instant deletedAt;

    // Construtor padrão
    public Coupon(String code, String description, BigDecimal discountValue, Instant expirationDate, Boolean published) {
        this.code = formatCode(code);
        this.description = description;
        this.discountValue = discountValue;
        this.expirationDate = expirationDate;
        this.published = (published != null) ? published : false;

        this.status = this.published ? CouponStatus.ACTIVE : CouponStatus.INACTIVE;

        validate();
    }

    @PrePersist
    private void prePersist() {
        // Garante valores padrões caso o JPA instancie o objeto sem passar pelos construtores
        if (this.published == null) this.published = false;
        if (this.status == null) this.status = CouponStatus.INACTIVE;
    }

    // Função responsável pelo Soft-Delete do cupom
    public void markAsDeleted() {
        if (this.status == CouponStatus.DELETED) {
            // Envia uma exception caso o cupom já esteja deletado
            throw new CouponBusinessException("Coupon is already deleted.");
        }
        this.status = CouponStatus.DELETED;
        this.deletedAt = Instant.now();
    }


    // Função auxiliar para validação das regras de negócio
    private void validate() {
        if (this.code == null || this.code.length() != 6) {
            // Envia uma exception caso o cupom seja inválido
            throw new CouponBusinessException("Invalid coupon code. It must be exactly 6 characters.");
        }
        if (this.expirationDate == null || this.expirationDate.isBefore(Instant.now())) {
            // Envia uma exception caso a data de expiração seja no passado
            throw new CouponBusinessException("Expiration date cannot be in the past.");
        }
        if (this.discountValue == null || this.discountValue.compareTo(new BigDecimal("0.5")) < 0) {
            // Envia uma exception caso o valor de desconto seja menor que 0.5
            throw new CouponBusinessException("Minimum discount value allowed is 0.5.");
        }
    }

    // Método responsável pela padronização do código do cupom (remoção de caracteres especiais)
    private String formatCode(String code) {
        return (code != null) ? code.replaceAll("[^a-zA-Z0-9]", "") : null;
    }
}