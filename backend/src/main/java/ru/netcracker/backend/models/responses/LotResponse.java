package ru.netcracker.backend.models.responses;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotResponse {
    private Long id;
    private String name;
    private String description;
    private Long minBank;
    private Long step;
    private String picture;
    private Long auctionId;
}
