package com.beans.cartservice.repository;

import com.beans.cartservice.entity.CartEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CartRepository extends JpaRepository<CartEntity, Long> {
    List<CartEntity> findByUserId(Long userId);
}
