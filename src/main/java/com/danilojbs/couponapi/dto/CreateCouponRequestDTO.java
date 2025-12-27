package com.danilojbs.couponapi.dto;

import jakarta.validation.constraints.*;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;
import java.time.Instant;

/**
 * DTO utilizado para capturar os dados na criação de um novo cupom.
 * Classe focada em mapear o JSON de entrada e aplicar validações iniciais.
 */
@Getter
@Setter
public class CreateCouponRequestDTO {

    // @NotBlank: Garante que o campo não seja nulo e não contenha apenas espaços vazios.
    @NotBlank(message = "Code is required")
    private String code;

    @NotBlank(message = "Description is required")
    private String description;

    // @NotNull: Campo obrigatório.
    // @DecimalMin: Valida a regra de negócio de valor mínimo (0.5).
    @NotNull(message = "Discount value is required")
    @DecimalMin(value = "0.5", message = "Minimum discount value is 0.5")
    private BigDecimal discountValue;

    // @Future: Garante que a data enviada seja posterior ao momento atual.
    @NotNull(message = "Expiration date is required")
    @Future(message = "Expiration date must be in the future")
    private Instant expirationDate;

    // Campo opcional; se for nulo, a lógica de negócio definirá o valor padrão como false).
    private Boolean published;
}