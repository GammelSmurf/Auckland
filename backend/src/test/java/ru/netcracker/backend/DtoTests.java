package ru.netcracker.backend;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.service.AuctionService;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

@ExtendWith(SpringExtension.class)
@SpringBootTest
class DtoTests {
    @Autowired
    private ModelMapper modelMapper;
    @Autowired
    private AuctionService auctionService;

    @Test
    @Disabled
    public void auctionDto() {
        DateTimeFormatter f1 = DateTimeFormatter.ofPattern( "yyyy-MM-dd HH:mm:ss" );
        DateTimeFormatter f2 = DateTimeFormatter.ofPattern( "HH:mm:ss" );

        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setName("string");
        auctionRequest.setBeginDate(LocalDateTime.parse("2021-12-07 14:00:00", f1));
        auctionRequest.setBoostTime(LocalTime.parse("00:02:00", f2));
        auctionRequest.setLotDuration(LocalTime.parse("00:02:00", f2));
        auctionRequest.setUsername("TEST");

        Auction auction = modelMapper.map(auctionRequest, Auction.class);

        Assertions.assertNull(auction.getId());

        AuctionResponse auctionResponse = modelMapper.map(auction, AuctionResponse.class);

        Assertions.assertEquals(auctionResponse.getName(), "string");
    }
}
