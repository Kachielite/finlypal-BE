package com.derrick.finlypal.entity;

import jakarta.persistence.*;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import lombok.*;
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
  private String email;
  private LocalDateTime expiryDate;

  @CreationTimestamp
  @Column(name = "created_at")
  private Timestamp createdAt;
}
