package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.ResetToken;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    ResetToken findByToken(String token);

    Optional<ResetToken> findByEmail(String email);
}
