package com.derrick.finlypal.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "reset_tokens")
public class ResetToken {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;

  private String token;

  @Min(1000) // Ensures OTP is at least 1000
  @Max(9999) // Ensures OTP is at most 9999
  private Integer otp;

  private String email;
  private LocalDateTime expiryDate;

  @CreationTimestamp
  @Column(name = "created_at")
  private Timestamp createdAt;
}
