package ru.netcracker.backend;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.models.requests.AuctionRequest;
import ru.netcracker.backend.models.responses.AuctionResponse;

import java.time.LocalDateTime;

@SpringBootTest
class DtoTests {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void auctionDto() {
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setName("string");
        auctionRequest.setBeginDate(LocalDateTime.of(1234, 5, 19, 5, 5));
        auctionRequest.setBoostTime(LocalDateTime.of(1234, 5, 19, 5, 5));
        auctionRequest.setLotDuration(1235);
        auctionRequest.setUserId(1L);

        Auction auction = modelMapper.map(auctionRequest, Auction.class);

        assertThat(auction.getId()).isNull();
        assertThat(auction.getUser().getId()).isNotNull();

        AuctionResponse auctionResponse = modelMapper.map(auction, AuctionResponse.class);

        assertThat(auctionResponse.getUserId()).isEqualTo(1L);
    }
}
