package com.derrick.finlypal.entity;

import com.derrick.finlypal.enums.SavingsStatus;
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
@Entity(name = "savings")
public class Savings {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Goal name is required")
    @Column(name = "goal_name")
    private String goalName;

    @NotNull(message = "Target amount is required")
    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @NotNull(message = "Target amount is required")
    @Column(name = "target_amount")
    private BigDecimal targetAmount;

    @NotNull(message = "Saved amount is required")
    @Column(name = "saved_amount")
    private BigDecimal savedAmount = BigDecimal.ZERO;

    @Enumerated(EnumType.STRING)
    private SavingsStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
}
