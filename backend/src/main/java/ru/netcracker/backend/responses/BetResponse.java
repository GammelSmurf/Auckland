package ru.netcracker.backend.responses;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Time;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetResponse {
    private BigDecimal currentBank;
}
