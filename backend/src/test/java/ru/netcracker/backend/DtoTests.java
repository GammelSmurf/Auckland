package ru.netcracker.backend;

import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.*;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.models.requests.AuctionRequest;
import ru.netcracker.backend.models.responses.AuctionResponse;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;

@SpringBootTest
class DtoTests {

    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void auctionDto() {
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setName("string");
        auctionRequest.setBeginDate(Timestamp.valueOf("2021-12-07 14:00:00"));
        auctionRequest.setBoostTime(Time.valueOf("00:02:00"));
        auctionRequest.setLotDuration(Time.valueOf("00:02:00"));
        auctionRequest.setUserId(1L);

        Auction auction = modelMapper.map(auctionRequest, Auction.class);

        assertThat(auction.getId()).isNull();
        assertThat(auction.getUser().getId()).isNotNull();

        AuctionResponse auctionResponse = modelMapper.map(auction, AuctionResponse.class);

        assertThat(auctionResponse.getUserId()).isEqualTo(1L);
    }
}
