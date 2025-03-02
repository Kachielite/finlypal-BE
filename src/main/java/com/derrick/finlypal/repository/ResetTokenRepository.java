package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.ResetToken;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ResetTokenRepository extends JpaRepository<ResetToken, Long> {
  ResetToken findByToken(String token);

  Optional<ResetToken> findByEmail(String email);

  @Transactional
  void deleteByEmail(String email);
}
