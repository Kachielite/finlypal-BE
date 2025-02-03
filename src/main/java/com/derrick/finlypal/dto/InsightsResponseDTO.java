package com.derrick.finlypal.dto;

import com.derrick.finlypal.enums.ExpenseType;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties(ignoreUnknown = true)
public class InsightsResponseDTO {
    private TotalSpend totalSpend;
    private List<SpendByCategory> spendByCategory;
    private List<SpendTrend> spendTrend;
    private List<MonthlyComparison> monthlyComparison;
    private List<TopExpenses> topExpenses;

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class TotalSpend {
        @JsonProperty("total_spend")
        private BigDecimal totalSpend;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class SpendByCategory {
        private String category;

        @JsonProperty("total_spend")
        @NotNull(message = "Amount is required")
        private BigDecimal totalSpend;

        private Integer percentage;
    }


    @Data
    @NoArgsConstructor
    public static class SpendTrend {
        private LocalDate date;
        private BigDecimal amount;

        public SpendTrend(LocalDate date, BigDecimal amount) {
            this.date = date;
            this.amount = amount;
        }
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class MonthlyComparison {
        private String month;
        @JsonProperty("total_spend")
        private BigDecimal totalSpend;
        private ExpenseType type;
    }

    @Getter
    @Setter
    @Builder
    @AllArgsConstructor
    @RequiredArgsConstructor
    public static class TopExpenses {
        private String description;
        private BigDecimal amount;
        private LocalDate date;
    }
}
