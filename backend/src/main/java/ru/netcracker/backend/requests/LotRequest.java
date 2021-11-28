package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LotRequest {
    @NotBlank(message="Lot's name should not be blank")
    @Size(min=3,max=255)
    private String name;
    @NotBlank(message="Lot's description should not be blank")
    @Size(min=3,max=10000)
    private String description;
    @Min(0)
    private Long minBank;
    @Min(0)
    private Long step;
    @NotBlank(message="Lot picture address should not be blank")
    @Size(min=3,max=255)
    private String picture;
    private Long auctionId;
}
