package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SubscribeRequest {
    @NotBlank(message="Subscribes name should not be blank")
    @Size(min=3,max=255)
    private String username;


    private Long auctionId;
}
