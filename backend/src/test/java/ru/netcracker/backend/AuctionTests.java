package ru.netcracker.backend;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.netcracker.backend.exception.ValidationException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.responses.LogResponse;
import ru.netcracker.backend.responses.SyncResponse;
import ru.netcracker.backend.service.AuctionService;
import ru.netcracker.backend.service.BetService;
import ru.netcracker.backend.util.ConsoleColors;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static java.util.concurrent.TimeUnit.SECONDS;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/test-data.sql"})
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class AuctionTests {
    @LocalServerPort
    private Integer port;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private BetService betService;
    @Autowired
    private AuctionService auctionService;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private AuctionRepository auctionRepository;

    private final long TEST_AUCTION_ID = 1;
    private final String TEST_USERNAME_1 = "test";
    private final String TEST_USERNAME_2 = "lol";
    private final String WEB_SOCKET_ENDPOINT_TEMPLATE = "ws://localhost:%d/ws";
    private final long TIME_MILLIS_DELAY_BEFORE_START = 5000;

    private WebSocketStompClient webSocketStompClient;
    private StompSession session;

    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        connectToWS();
    }

    private void connectToWS() throws ExecutionException, InterruptedException, TimeoutException {
        session = webSocketStompClient
                .connect(
                        String.format(WEB_SOCKET_ENDPOINT_TEMPLATE, port),
                        new StompSessionHandlerAdapter() {
                        })
                .get(1, SECONDS);
    }

    @Test
    @Disabled
    public void testSubscribe() {
        auctionService.subscribe(TEST_USERNAME_1, TEST_AUCTION_ID);
        Assertions.assertFalse(
                userRepository
                        .findByUsername(TEST_USERNAME_1).get()
                        .getOwnAuctions().isEmpty());
    }

    @Test
    @Disabled
    public void testAuction() throws ValidationException, InterruptedException {
        session.subscribe(
                String.format("/auction/state/%d", TEST_AUCTION_ID),
                new StompFrameHandler() {
                    @Override
                    @NonNull
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return BetResponse.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                        System.out.println("Received message: " + payload);
                    }
                });
        session.subscribe(
                String.format("/auction/logs/%d", TEST_AUCTION_ID),
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return LogResponse.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                        System.out.println("Received log message: " + payload);
                    }
                });

        auctionService.subscribe(TEST_USERNAME_1, TEST_AUCTION_ID);
        auctionService.subscribe(TEST_USERNAME_2, TEST_AUCTION_ID);

        auctionService.makeAuctionWaiting(TEST_AUCTION_ID);
        setTestDates(TEST_AUCTION_ID);

        printSync(betService.sync(TEST_AUCTION_ID));
        Thread.sleep(TIME_MILLIS_DELAY_BEFORE_START);

        printSync(betService.sync(TEST_AUCTION_ID));

        betService.makeBet(TEST_USERNAME_1, TEST_AUCTION_ID, new BigDecimal(4000));
        betService.makeBet(TEST_USERNAME_2, TEST_AUCTION_ID, new BigDecimal(6000));

        Thread.sleep(TIME_MILLIS_DELAY_BEFORE_START * 2);
        printSync(betService.sync(TEST_AUCTION_ID));

        Thread.sleep(TIME_MILLIS_DELAY_BEFORE_START * 2);
        printSync(betService.sync(TEST_AUCTION_ID));
        betService.makeBet(TEST_USERNAME_1, TEST_AUCTION_ID, new BigDecimal(8000));

        Thread.sleep(TIME_MILLIS_DELAY_BEFORE_START * 2);
        printSync(betService.sync(TEST_AUCTION_ID));
        betService.makeBet(TEST_USERNAME_2, TEST_AUCTION_ID, new BigDecimal(10000));

        Thread.sleep(TIME_MILLIS_DELAY_BEFORE_START * 2);
        printSync(betService.sync(TEST_AUCTION_ID));
    }

    @Test
    public void testUpdatedAuction() {
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setCreatorUsername(TEST_USERNAME_1);
        auctionRequest.setBeginDate(LocalDateTime.now());
        auctionRequest.setBoostTime(LocalTime.now());
        auctionRequest.setUsersLimit(5);
        auctionRequest.setDescription("desc");
        auctionRequest.setName("AUCTION");
        Auction auction = modelMapper.map(auctionRequest, Auction.class);

        System.out.println(auction.getCreator() != null);

        auctionService.updateAuction(TEST_AUCTION_ID, auction);
    }

    public void printSync(SyncResponse syncResponse) {
        System.out.println(ConsoleColors.GREEN +
                (syncResponse.getUntil() ? "duration_until: " : "duration_after: ") + syncResponse.getTimeUntil() +
                ", current_lot: " + syncResponse.getCurrentLot().getName() +
                ", status: " + syncResponse.getAuctionStatus() +
                ", changed: " + syncResponse.getChanged() +
                ConsoleColors.RESET);
    }

    private void setTestDates(Long auctionId) {
        Auction auction = auctionRepository.findById(auctionId).get();
        auction.setBeginDate(LocalDateTime.now()
                .plus(TIME_MILLIS_DELAY_BEFORE_START, ChronoUnit.MILLIS));
        auctionRepository.save(auction);
    }
}
