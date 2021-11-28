package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.DecimalMin;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class BetRequest {
    @NotBlank(message="Username should not be blank")
    @Size(min=3,max=255)
    private String username;

    @DecimalMin(value = "0.01")
    private BigDecimal currentBank;
}
