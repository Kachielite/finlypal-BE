package com.derrick.finlypal.entity;

import com.derrick.finlypal.enums.BudgetItemStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "budget_item")
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

    @ManyToOne
    @JoinColumn(name = "buget_id")
    private Budget budget;
}
