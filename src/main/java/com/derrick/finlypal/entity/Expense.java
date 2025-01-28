package com.derrick.finlypal.entity;

import com.derrick.finlypal.enums.Category;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.UpdateTimestamp;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "expenses")
public class Expense {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;

    private String description;

    private BigDecimal amount;

    private LocalDate date;

    @Enumerated(EnumType.STRING)
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