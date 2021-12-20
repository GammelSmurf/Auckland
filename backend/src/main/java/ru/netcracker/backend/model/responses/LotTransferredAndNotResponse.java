package ru.netcracker.backend.model.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotTransferredAndNotResponse {
    private List<LotResponse> transferredLots;
    private List<LotResponse> notTransferredLots;
}
