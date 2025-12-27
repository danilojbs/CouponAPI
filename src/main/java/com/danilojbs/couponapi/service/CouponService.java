package com.danilojbs.couponapi.service;

import com.danilojbs.couponapi.domain.Coupon;
import com.danilojbs.couponapi.domain.exception.CouponNotFoundException;
import com.danilojbs.couponapi.dto.CouponResponseDTO;
import com.danilojbs.couponapi.dto.CreateCouponRequestDTO;
import com.danilojbs.couponapi.repository.CouponRepository;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;


/**
 * Classe de Serviço: Contém a lógica do serviço.
 * Onde tudo (DTOs, Entidades e Repositórios) se conectam.
 */
@Service
@AllArgsConstructor
public class CouponService {

    private final CouponRepository repository;


    /**
     * Fluxo de Criação:
     * 1 - Recebe o DTO de entrada.
     * 2 - Instancia a Entidade do Cupom (realizando as validações).
     * 3 - Salva no banco através do repository.
     * 4 - Converte o resultado para o DTO de resposta (ResponseDTO).
     */
    public CouponResponseDTO create(CreateCouponRequestDTO request) {
        Coupon coupon = new Coupon(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.getPublished()
        );

        return CouponResponseDTO.from(repository.save(coupon));

    }

    /**
     * Fluxo de exclusão (Soft Delete):
     * Transactional: Garante que a operação seja atômica. Se algo falhar, o banco sofre rollback.
     * (Transactional foi um novo aprendizado durante o desafio)
     */
    @Transactional
    public void delete(String id) {
        Coupon coupon = repository.findById(id)
                .orElseThrow(() -> new CouponNotFoundException(id));

        coupon.markAsDeleted();
        repository.save(coupon);
    }

}
