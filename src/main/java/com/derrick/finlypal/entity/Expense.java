package com.derrick.finlypal.entity;

import com.derrick.finlypal.enums.ExpenseType;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import lombok.*;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "expenses")
public class Expense {
  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull(message = "Description is required")
  private String description;

  @NotNull(message = "Amount is required")
  private BigDecimal amount = BigDecimal.ZERO;

  @NotNull(message = "Date is required")
  private LocalDate date;

  @NotNull(message = "Expense type is required")
  @Enumerated(EnumType.STRING)
  private ExpenseType type;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private Category category;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @Column(name = "created_at")
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Timestamp updatedAt;
}
