package ru.netcracker.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.responses.AuctionResponse;

import java.sql.Time;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DtoTests {
    @Autowired
    private ModelMapper modelMapper;

    @Test
    public void auctionDto() {
        DateTimeFormatter f = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );

        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setName("string");
        auctionRequest.setBeginDate(LocalDateTime.parse("2021-12-07 14:00:00", f));
        auctionRequest.setBoostTime(LocalTime.parse("00:02:00", f));
        auctionRequest.setLotDuration(LocalTime.parse("00:02:00", f));
        auctionRequest.setUserId(1L);

        Auction auction = modelMapper.map(auctionRequest, Auction.class);

        Assertions.assertNull(auction.getId());
        Assertions.assertNotNull(auction.getUser().getId());

        AuctionResponse auctionResponse = modelMapper.map(auction, AuctionResponse.class);

        Assertions.assertEquals(auctionResponse.getUserId(), 1L);
    }
}
