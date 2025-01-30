package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    ResetToken findByToken(String token);
}
