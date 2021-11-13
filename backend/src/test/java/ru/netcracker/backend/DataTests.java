package ru.netcracker.backend;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.LotRepository;

import java.util.Optional;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/test-data.sql"})
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class DataTests {
    @Autowired
    private AuctionRepository auctionRepository;
    @Autowired
    private LotRepository lotRepository;

    private final long TEST_AUCTION_ID = 1;

    @Test
    public void testAuctionData() {
        Optional<Auction> auctionOptional = auctionRepository.findById(TEST_AUCTION_ID);
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            saveLotFinishWithWinner(auction);

            Optional<Lot> lotOptional = lotRepository.findById(TEST_AUCTION_ID);
            if (lotOptional.isPresent()) {
                Lot lot = lotOptional.get();
                Assertions.assertNotNull(lot.getWinner());
            }
        }
    }

    @Test
    public void testLotFinished() {
        Optional<Auction> auctionOptional = auctionRepository.findById(TEST_AUCTION_ID);
        if (auctionOptional.isPresent()) {
            Auction auction = auctionOptional.get();
            saveLotFinish(auction);
            auction.getBet().getLot().setFinished(true);

            Optional<Lot> lotOptional = lotRepository.findById(TEST_AUCTION_ID);
            if (lotOptional.isPresent()) {
                Lot lot = lotOptional.get();
                Assertions.assertTrue(lot.isFinished());
            }
        }
    }

    private void saveLotFinishWithWinner(Auction auction) {
        auction.getBet().getLot().setWinner(auction.getBet().getUser());
        saveLotFinish(auction);
    }

    private void saveLotFinish(Auction auction) {
        auction.getBet().getLot().setFinished(true);
        lotRepository.save(auction.getBet().getLot());
    }
}
