package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.ResetToken;
import jakarta.transaction.Transactional;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
    ResetToken findByToken(String token);

    Optional<ResetToken> findByEmail(String email);

    Optional<ResetToken> findByOtpAndEmail(@Min(1000) @Max(9999) Integer otp, String email);

    Optional<ResetToken> findByTokenAndEmail(String token, String email);

    @Transactional
    void deleteByEmail(String email);
}
