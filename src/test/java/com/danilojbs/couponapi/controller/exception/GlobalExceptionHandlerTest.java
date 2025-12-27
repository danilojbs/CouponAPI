package com.danilojbs.couponapi.controller.exception;

import com.danilojbs.couponapi.controller.CouponController;
import com.danilojbs.couponapi.domain.exception.CouponBusinessException;
import com.danilojbs.couponapi.domain.exception.CouponNotFoundException;
import com.danilojbs.couponapi.service.CouponService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.mockito.Mockito.doThrow;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Teste de Unidade do Handler de Exceções.
 * Responsável por garantir que as exceções lançadas pelo Service/Domínio
 * sejam convertidas corretamente em respostas HTTP (JSON) pelo GlobalExceptionHandler.
 */
class GlobalExceptionHandlerTest {

    private MockMvc mockMvc; // Simula requisições HTTP sem subir o servidor completo

    @Mock
    private CouponService service; // Mock do serviço para simulação dos cenários

    @InjectMocks
    private CouponController controller;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        // CONFIGURAÇÃO CHAVE: Registra o Handler manualmente no MockMvc.
        // Sem o setControllerAdvice, o MockMvc ignoraria o Handler e o teste falharia.
        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .build();
    }

    // Testa o cenário onde o DTO é inválido (ex: código do cupom vazio).
    @Test
    void handleValidationErrors() throws Exception {
        // Envia JSON inválido para cair no handleValidationExceptions
        mockMvc.perform(post("/api/coupons")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"code\": \"\"}")) // JSON inválido
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(jsonPath("$.errors").exists()) // Verifica se a chave 'errors' está no JSON
                .andExpect(jsonPath("$.message").value("Validation failed"));
    }

    // Testa erros de regra de negócio (ex: cupom já deletado).
    @Test
    void handleBusinessErrors() throws Exception {
        // Simula o service lançando uma exceção de negócio
        doThrow(new CouponBusinessException("Business Error")).when(service).delete("1");

        mockMvc.perform(delete("/api/coupons/1"))
                .andExpect(status().isBadRequest()) // HTTP 400
                .andExpect(jsonPath("$.errors").doesNotExist())  // Verifica se a chave 'errors' não está no JSON
                .andExpect(jsonPath("$.message").value("Business Error"));
    }

    /**
     * Testa o cenário de recurso não encontrado.
     * Garante que a CouponNotFoundException resulte em HTTP 404.
     */
    @Test
    void handleNotFound() throws Exception {
        doThrow(new CouponNotFoundException("1")).when(service).delete("1");

        mockMvc.perform(delete("/api/coupons/1"))
                .andExpect(status().isNotFound()) // HTTP 404
                .andExpect(jsonPath("$.error").value("Not Found"));
    }
}
