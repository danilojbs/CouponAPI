package com.danilojbs.couponapi.controller;

import com.danilojbs.couponapi.dto.CouponResponseDTO;
import com.danilojbs.couponapi.dto.CreateCouponRequestDTO;
import com.danilojbs.couponapi.service.CouponService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/coupons")
@AllArgsConstructor
public class CouponController {

    private final CouponService service;

    // Requisição da criação de cupons
    @PostMapping
    public ResponseEntity<CouponResponseDTO> create(@Valid @RequestBody CreateCouponRequestDTO request) {
        var response = service.create(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // Requisição para exclusão do cupom (Soft-Delete)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        service.delete(id);
        return ResponseEntity.noContent().build();
    }

}
