package com.danilojbs.couponapi.repository;

import com.danilojbs.couponapi.domain.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

// Interface responsável pela comunicação com o Banco de Dados.
@Repository
public interface CouponRepository extends JpaRepository<Coupon, String> {
}
