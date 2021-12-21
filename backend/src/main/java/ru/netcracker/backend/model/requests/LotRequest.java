package ru.netcracker.backend.model.requests;

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
    @Size(min=3,max=1000)
    private String name;
    @NotBlank(message="Lot's description should not be blank")
    @Size(min=3,max=10000)
    private String description;
    @Min(0)
    private Long minPrice;
    @Min(0)
    private Long priceIncreaseMinStep;

    private String pictureLink;
    private Long auctionId;
}
