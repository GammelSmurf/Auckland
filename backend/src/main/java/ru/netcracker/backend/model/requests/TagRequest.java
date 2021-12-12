package ru.netcracker.backend.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class TagRequest {
    @NotBlank(message="Lot's name should not be blank")
    @Size(min=3,max=255)
    private String name;

    private Long categoryId;
    private Long auctionId;
}
