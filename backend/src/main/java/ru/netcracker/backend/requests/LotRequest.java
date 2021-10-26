package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotRequest {
    private String name;
    private String description;
    private Long minBank;
    private Long step;
    private String picture;
    private Long auctionId;
}
