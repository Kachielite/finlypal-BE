package com.derrick.finlypal.entity;

import com.derrick.finlypal.enums.BudgetStatus;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.CreatedDate;

import java.math.BigDecimal;
import java.sql.Date;
import java.sql.Timestamp;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Entity
@Table(name = "budgets")
public class Budget {

    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    @NotNull(message = "Name is required")
    private String name;

    @NotNull(message = "Start date is required")
    @Column(name = "start_date")
    private Date startDate;

    @NotNull(message = "End date is required")
    @Column(name = "end_date")
    private Date endDate;

    @NotNull(message = "Total Budget is required")
    @Column(name = "total_budget")
    private BigDecimal totalBudget;

    @NotNull(message = "Status is required")
    @Enumerated(EnumType.STRING)
    private BudgetStatus status;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @CreatedDate
    @Column(name = "created_at")
    private Timestamp createdAt;

    @CreatedDate
    @Column(name = "updated_at")
    private Timestamp updatedAt;
}
