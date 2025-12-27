package com.danilojbs.couponapi.controller;

import com.danilojbs.couponapi.controller.exception.GlobalExceptionHandler;
import com.danilojbs.couponapi.dto.CouponResponseDTO;
import com.danilojbs.couponapi.dto.CreateCouponRequestDTO;
import com.danilojbs.couponapi.service.CouponService;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.math.BigDecimal;
import java.time.Instant;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

/**
 * Teste do Controller: Valida os endpoints da API.
 * Garante que o mapeamento de URLs, a conversão de JSON e o retorno HTTP estão corretos.
 */
class CouponControllerTest {

    private MockMvc mockMvc; // Utilizado para simular chamadas HTTP aos endpoints

    @Mock
    private CouponService service; // Mock do serviço para simulação dos cenários

    @InjectMocks
    private CouponController controller;

    private ObjectMapper objectMapper; // Ferramenta para converter objetos Java em JSON String

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // standaloneSetup: Configura o MockMvc apenas para este Controller.
        // Inclui o GlobalExceptionHandler para que o teste seja realista com o comportamento da aplicação.
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
        // Configuração do Jackson para lidar com a data Instant
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
    }

    /**
     * Teste de Sucesso na Criação do Cupom:
     * Valida se ao enviar um JSON válido, a API retorna 201 (Created) e o corpo correto.
     */
    @Test
    void testCreateCoupon() throws Exception {
        // Montagem do cenário (RequestDTO)
        CreateCouponRequestDTO request = new CreateCouponRequestDTO();
        request.setCode("ABC123");
        request.setDescription("Cupom Teste");
        request.setDiscountValue(BigDecimal.valueOf(0.5));
        request.setExpirationDate(Instant.now().plusSeconds(3600));
        request.setPublished(true);

        // Criação do Response esperado usando o método estático 'from' do Record
        CouponResponseDTO response = CouponResponseDTO.from(
                new com.danilojbs.couponapi.domain.Coupon(
                        request.getCode(),
                        request.getDescription(),
                        request.getDiscountValue(),
                        request.getExpirationDate(),
                        request.getPublished()
                )
        );

        // Mockito: Quando o service for chamado, retorne o DTO de resposta criado anteriormente
        when(service.create(any(CreateCouponRequestDTO.class))).thenReturn(response);

        // Execução e Validação
        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.code").value("ABC123"))
                .andExpect(jsonPath("$.description").value("Cupom Teste"));
    }

    /**
     * Teste de Sucesso na exclusão:
     * Valida se a API retorna 204 (No Content) após excluir com sucesso (Soft-Delete).
     */
    @Test
    void testDeleteCoupon() throws Exception {
        String id = "1";
        doNothing().when(service).delete(id);

        mockMvc.perform(delete("/api/coupons/{id}", id))
                .andExpect(status().isNoContent()); // HTTP 204
    }
}
