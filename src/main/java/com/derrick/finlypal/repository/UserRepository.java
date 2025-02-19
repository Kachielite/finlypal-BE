package com.derrick.finlypal.repository;

import com.derrick.finlypal.entity.User;
import jakarta.transaction.Transactional;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
@Transactional
public interface UserRepository extends JpaRepository<User, Long> {
  Optional<User> findByEmail(String email);
}
