package com.derrick.finlypal.entity;

import com.derrick.finlypal.enums.BudgetItemStatus;
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
@Table(name = "budget_items")
public class BudgetItem {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;

  @NotNull(message = "Name is required")
  private String name;

  @NotNull(message = "Allocated amount is required")
  @Column(name = "allocated_amount")
  private BigDecimal allocatedAmount;

  @NotNull(message = "Budget item is required")
  @Enumerated(EnumType.STRING)
  private BudgetItemStatus status;

  @ManyToOne(cascade = {CascadeType.PERSIST, CascadeType.MERGE})
  @JoinColumn(name = "budget_id", nullable = false)
  private Budget budget;

  @OneToMany(mappedBy = "budgetItem", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Expense> expenses;

  @CreationTimestamp
  @Column(name = "created_at")
  private Timestamp createdAt;

  @UpdateTimestamp
  @Column(name = "updated_at")
  private Timestamp updatedAt;
}
