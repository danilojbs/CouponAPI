package com.danilojbs.couponapi.repository;

import com.danilojbs.couponapi.domain.Coupon;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Teste de Repositório: Focado na camada de persistência.
 * Utilização de banco de dados em memória (H2) para validar as operações.
 */
@DataJpaTest // Anotação que configura um ambiente JPA leve, usando banco em memória
class CouponRepositoryTest {

    @Autowired
    private CouponRepository repository;

    /**
     * Valida se a entidade está sendo salva e recuperada corretamente.
     * Também verifica se a regra de formatação do cupom persiste no banco.
     */
    @Test
    void testFindById() {
        // Criação de um cupom com caracteres especiais ("-").
        // O construtor da classe Coupon deve limpar o código antes de salvar.
        Coupon coupon = new Coupon(
                "ABC-123",
                "Cupom Teste",
                BigDecimal.valueOf(0.5),
                Instant.now().plusSeconds(3600),
                true
        );

        // Salva no banco de dados
        Coupon saved = repository.save(coupon);

        // Busca o cupom salvo pelo ID gerado
        Optional<Coupon> found = repository.findById(saved.getId());

        // Verifica se o objeto foi encontrado.
        assertThat(found).isPresent();

        // Valida se o código no banco está formatado ("ABC123"), comprovando que
        // a lógica ocorreu antes da persistência.
        assertThat(found.get().getCode()).isEqualTo("ABC123");
    }
}

