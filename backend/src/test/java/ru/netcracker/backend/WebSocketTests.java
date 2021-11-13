package ru.netcracker.backend;

import io.zonky.test.db.AutoConfigureEmbeddedDatabase;
import lombok.NonNull;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
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
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.requests.BetRequest;
import ru.netcracker.backend.responses.LogResponse;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.service.AuctionService;
import ru.netcracker.backend.service.BetService;

import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Sql({"/test-data.sql"})
@AutoConfigureEmbeddedDatabase(refresh = AFTER_EACH_TEST_METHOD)
public class WebSocketTests {
    @LocalServerPort private Integer port;

    private final long TEST_ID = 1;
    private final String TEST_USERNAME = "test";
    private final String WEB_SOCKET_ENDPOINT_TEMPLATE = "ws://localhost:%d/ws";

    private WebSocketStompClient webSocketStompClient;
    private StompSession session;

    @Autowired
    private AuctionService auctionService;
    @Autowired
    private BetService betService;

    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        webSocketStompClient =
                new WebSocketStompClient(
                        new SockJsClient(
                                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        connectToWS();
    }

    @Test
    public void testConnection() {
        assertTrue(session.isConnected());
    }

    private void connectToWS() throws ExecutionException, InterruptedException, TimeoutException {
        session =
                webSocketStompClient
                        .connect(
                                String.format(WEB_SOCKET_ENDPOINT_TEMPLATE, port),
                                new StompSessionHandlerAdapter() {})
                        .get(1, SECONDS);
    }

    @Test
    public void testMakingBet() throws InterruptedException, ValidationException {
        ArrayBlockingQueue<BetResponse> blockingStateQueue = new ArrayBlockingQueue<>(1);
        auctionService.makeAuctionWaiting(TEST_ID);

        session.subscribe(
                String.format("/auction/state/%d", TEST_ID),
                new StompFrameHandler() {
                    @Override
                    @NonNull
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return BetResponse.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                        System.out.println("Received message: " + payload);
                        blockingStateQueue.add((BetResponse) payload);
                    }
                });

        BetRequest betRequest = new BetRequest();
        betRequest.setCurrentBank(new BigDecimal(30000));
        betRequest.setUsername(TEST_USERNAME);

        BetResponse betResponse = new BetResponse();
        betResponse.setCurrentBank(new BigDecimal(30000));
        betRequest.setUsername(TEST_USERNAME);


        betService.syncBeforeRun(TEST_ID);

        Thread.sleep(100);
        session.send(String.format("/app/play/%d", TEST_ID), betRequest);

        assertEquals(betResponse, blockingStateQueue.poll(1, SECONDS));
    }

    @Test
    public void testAuctionLogger() throws InterruptedException, ValidationException {
        ArrayBlockingQueue<LogResponse> blockingLogQueue = new ArrayBlockingQueue<>(2);
        auctionService.makeAuctionWaiting(TEST_ID);

        session.subscribe(
                String.format("/auction/logs/%d", TEST_ID),
                new StompFrameHandler() {
                    @Override
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return LogResponse.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                        System.out.println("Received log message: " + payload);
                        blockingLogQueue.add((LogResponse) payload);
                    }
                });

        BetRequest betRequest = new BetRequest();
        betRequest.setCurrentBank(new BigDecimal(30000));
        betRequest.setUsername(TEST_USERNAME);

        betService.syncBeforeRun(TEST_ID);
        session.send(String.format("/app/play/%d", TEST_ID), betRequest);

        Assertions.assertEquals(
                "-Status change- Статус аукциона изменился на RUNNING",
                blockingLogQueue.poll(1, SECONDS).getLogMessage());
        Assertions.assertEquals(
                "-Bet- test повысил ставку до 30000",
                blockingLogQueue.poll(1, SECONDS).getLogMessage());
    }
}
