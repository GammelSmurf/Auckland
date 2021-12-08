package ru.netcracker.backend;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.netcracker.backend.controller.AuctionController;
import ru.netcracker.backend.controller.AdminController;
import ru.netcracker.backend.requests.AuctionRequest;

import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class ValidationTests {
    @LocalServerPort
    private Integer port;

    @Autowired
    private AuctionController auctionController;
    @Autowired
    private AdminController adminController;

    private final long TEST_AUCTION_ID = 1;
    private final long TEST_LOT_ID = 1;
    private final String TEST_USERNAME_1 = "test";
    private final String TEST_USERNAME_2 = "lol";
    private final String WEB_SOCKET_ENDPOINT_TEMPLATE = "ws://localhost:%d/ws";
    private WebSocketStompClient webSocketStompClient;
    private StompSession session;
    @BeforeEach
    public void setUp() throws ExecutionException, InterruptedException, TimeoutException {
        webSocketStompClient =
                new WebSocketStompClient(
                        new SockJsClient(
                                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());
        connectToWS();
    }
    private void connectToWS() throws ExecutionException, InterruptedException, TimeoutException {
        session =
                webSocketStompClient
                        .connect(
                                String.format(WEB_SOCKET_ENDPOINT_TEMPLATE, port),
                                new StompSessionHandlerAdapter() {})
                        .get(10, SECONDS);
    }

    @Test
    public void testUpdatedAuction() {
        AuctionRequest auctionRequest = new AuctionRequest();
        auctionRequest.setCreatorUsername(TEST_USERNAME_1);
        auctionRequest.setBeginDate(LocalDateTime.now());
        auctionRequest.setBoostTime(LocalTime.now());
        auctionRequest.setUsersLimit(5);
        auctionRequest.setDescription(" ");
        auctionRequest.setName("AUCTION_UPDATE_2");
        System.out.println(auctionRequest);
        auctionController.updateAuction(TEST_AUCTION_ID,auctionRequest);
        //System.out.println(auctionController.getAllOwnAuctions("  ", PageRequest.of(0,10)));
        adminController.banUser("  ");
    }
}
