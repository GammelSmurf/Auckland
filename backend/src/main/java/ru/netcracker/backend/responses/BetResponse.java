package ru.netcracker.backend.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetResponse {
    private BigDecimal currentBank;
    private Long secondsUntil;
}
