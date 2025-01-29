package com.derrick.finlypal.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.CreationTimestamp;

import java.sql.Timestamp;
import java.time.LocalDateTime;

@Getter
@Setter
@Entity
@Table(name = "reset_tokens")
public class ResetToken {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Integer id;

    private String token;
    private Integer userId;
    private LocalDateTime expiryDate;

    @CreationTimestamp
    @Column(name = "created_at")
    private Timestamp createdAt;
}

