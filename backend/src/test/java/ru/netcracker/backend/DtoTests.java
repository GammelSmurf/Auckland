package ru.netcracker.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.netcracker.backend.model.auction.Auction;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.responses.AuctionResponse;

import java.sql.Time;
import java.sql.Timestamp;

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

        Assertions.assertNull(auction.getId());
        Assertions.assertNotNull(auction.getUser().getId());

        AuctionResponse auctionResponse = modelMapper.map(auction, AuctionResponse.class);

        Assertions.assertEquals(auctionResponse.getUserId(), 1L);
    }
}
