package com.derrick.finlypal.entity;

import com.derrick.finlypal.enums.SavingsStatus;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "savings")
public class Savings {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull(message = "Goal name is required")
  @Column(name = "goal_name")
  private String goalName;

  @NotNull(message = "Icon is required")
  private String icon;

  @NotNull(message = "Target amount is required")
  @Column(name = "target_amount")
  private BigDecimal targetAmount;

  @NotNull(message = "Saved amount is required")
  @Column(name = "saved_amount")
  private BigDecimal savedAmount = BigDecimal.ZERO;

  @NotNull(message = "Start date is required")
  @Column(name = "start_date")
  private LocalDate startDate;

  @NotNull(message = "End date is required")
  @Column(name = "end_date")
  private LocalDate endDate;

  @Enumerated(EnumType.STRING)
  private SavingsStatus status;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  @OneToMany(mappedBy = "savings", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Expense> expenses;

  @CreationTimestamp
  @Column(name = "created_at", updatable = false)
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Timestamp updatedAt;
}
