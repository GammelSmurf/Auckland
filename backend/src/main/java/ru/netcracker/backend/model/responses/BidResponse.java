package ru.netcracker.backend.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BidResponse {
    private BigDecimal currentBank;
    private Long secondsUntil;
}
