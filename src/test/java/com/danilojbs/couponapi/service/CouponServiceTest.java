package com.danilojbs.couponapi.service;

import com.danilojbs.couponapi.domain.Coupon;
import com.danilojbs.couponapi.domain.CouponStatus;
import com.danilojbs.couponapi.domain.exception.CouponBusinessException;
import com.danilojbs.couponapi.dto.CouponResponseDTO;
import com.danilojbs.couponapi.dto.CreateCouponRequestDTO;
import com.danilojbs.couponapi.repository.CouponRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

/**
 * Teste de Unidade do Serviço: Foca 100% nas regras de negócio.
 * Utiliza Mockito para simular o comportamento do Repositório.
 */
class CouponServiceTest {

    @Mock
    private CouponRepository repository; // Simula o banco de dados

    @InjectMocks
    private CouponService service; // Mock do serviço para simulação dos cenários

    private CreateCouponRequestDTO request;
    private Coupon coupon;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this); // Inicializa os Mocks antes de cada teste

        // Cenário padrão de entrada
        request = new CreateCouponRequestDTO();
        request.setCode("ABC-123");
        request.setDescription("Cupom Teste");
        request.setDiscountValue(BigDecimal.valueOf(0.5));
        request.setExpirationDate(Instant.now().plusSeconds(3600));
        request.setPublished(true);

        // Objeto de domínio esperado
        coupon = new Coupon(
                request.getCode(),
                request.getDescription(),
                request.getDiscountValue(),
                request.getExpirationDate(),
                request.getPublished()
        );
        coupon.setStatus(CouponStatus.INACTIVE); // valor inicial
    }

    /**
     * Testa se o serviço cria o cupom e retorna o DTO correto.
     * Verifica se a formatação de "ABC-123" para "ABC123" ocorreu com sucesso.
     */
    @Test
    void testCreateCoupon() {
        // any(Coupon.class) garante que qualquer objeto Coupon passado para o save seja aceito
        when(repository.save(any(Coupon.class))).thenAnswer(invocation -> {
            Coupon c = invocation.getArgument(0);
            return c;
        });

        CouponResponseDTO response = service.create(request);

        assertThat(response).isNotNull();
        assertThat(response.code()).isEqualTo("ABC123"); // Valida a formatação
        assertThat(response.description()).isEqualTo("Cupom Teste");

        verify(repository, times(1)).save(any(Coupon.class)); // Garante que salvou no banco
    }


    /**
     * Testa o Soft Delete com sucesso.
     * Verifica se o status mudou para DELETED e se a data foi preenchida.
     */
    @Test
    void testDeleteCoupon_Success() {
        coupon.setStatus(CouponStatus.INACTIVE);

        when(repository.findById("1")).thenReturn(Optional.of(coupon));
        when(repository.save(any(Coupon.class))).thenReturn(coupon);

        service.delete("1");

        assertThat(coupon.getStatus()).isEqualTo(CouponStatus.DELETED);
        assertThat(coupon.getDeletedAt()).isNotNull();
        verify(repository, times(1)).save(coupon);
    }

    @Test
    void testDeleteCoupon_NotFound() {
        when(repository.findById("1")).thenReturn(Optional.empty());

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete("1"));

        assertThat(exception.getMessage()).isEqualTo("Coupon not found with id: 1");
    }

    @Test
    void testDeleteCoupon_AlreadyDeleted() {
        coupon.setStatus(CouponStatus.DELETED);
        when(repository.findById("1")).thenReturn(Optional.of(coupon));

        RuntimeException exception = assertThrows(RuntimeException.class, () -> service.delete("1"));

        assertThat(exception.getMessage()).isEqualTo("Coupon is already deleted.");
    }

    /**
     * Testes de Falha (Negative Path):
     * Garantem que o sistema barra dados inválidos que violam as regras.
     */
    @Test
    void testCreateCoupon_InvalidDiscount() {
        request.setDiscountValue(new BigDecimal("0.4"));

        // Valida que a exceção de negócio sobe quando o valor é menor que 0.5
        assertThrows(CouponBusinessException.class, () -> service.create(request),
                "Minimum discount value allowed is 0.5.");
    }

    @Test
    void testCreateCoupon_PastExpiration() {
        request.setExpirationDate(Instant.now().minusSeconds(10));

        assertThrows(CouponBusinessException.class, () -> service.create(request),
                "Expiration date cannot be in the past.");
    }

    @Test
    void testCreateCoupon_InvalidCodeLength() {
        request.setCode("A-1@3"); // Resultaria em "A13" (apenas 3 caracteres)

        // Valida que o domínio rejeita códigos que, após limpos, não tenham 6 dígitos
        assertThrows(CouponBusinessException.class, () -> service.create(request));
    }
}
