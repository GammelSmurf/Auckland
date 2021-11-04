package ru.netcracker.backend;

import lombok.NonNull;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.web.server.LocalServerPort;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.simp.stomp.StompFrameHandler;
import org.springframework.messaging.simp.stomp.StompHeaders;
import org.springframework.messaging.simp.stomp.StompSession;
import org.springframework.messaging.simp.stomp.StompSessionHandlerAdapter;
import org.springframework.web.socket.client.standard.StandardWebSocketClient;
import org.springframework.web.socket.messaging.WebSocketStompClient;
import org.springframework.web.socket.sockjs.client.SockJsClient;
import org.springframework.web.socket.sockjs.client.WebSocketTransport;
import ru.netcracker.backend.requests.AuctionProcessRequest;
import ru.netcracker.backend.responses.AuctionProcessResponse;

import java.lang.reflect.Type;
import java.sql.Time;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@Slf4j
public class WebSocketTests {
    @LocalServerPort
    private Integer port;

    private long testId = 1;

    private WebSocketStompClient webSocketStompClient;

    @BeforeEach
    public void setUp() {
        webSocketStompClient = new WebSocketStompClient(new SockJsClient(
                List.of(new WebSocketTransport(new StandardWebSocketClient()))));
    }

    @Test
    public void testConnection() throws ExecutionException, InterruptedException, TimeoutException {
        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);
        System.out.println("Test port is: " + port);
        assertTrue(session.isConnected());
    }

    //@Test
    public void testMakingAuctionProcesses() throws ExecutionException, InterruptedException, TimeoutException {
        ArrayBlockingQueue<AuctionProcessResponse> blockingQueue = new ArrayBlockingQueue<>(1);

        webSocketStompClient.setMessageConverter(new MappingJackson2MessageConverter());

        StompSession session = webSocketStompClient
                .connect(String.format("ws://localhost:%d/ws", port), new StompSessionHandlerAdapter() {})
                .get(1, SECONDS);

        session.subscribe(String.format("/auction/state/%d", testId) , new StompFrameHandler() {

            @Override
            public Type getPayloadType(@NonNull StompHeaders headers) {
                return AuctionProcessResponse.class;
            }

            @Override
            public void handleFrame(@NonNull StompHeaders headers, Object payload) {
                System.out.println("Received message: " + payload);
                blockingQueue.add((AuctionProcessResponse) payload);
            }
        });

        AuctionProcessRequest auctionProcessRequest = new AuctionProcessRequest();
        auctionProcessRequest.setCurrentBank(10000L);
        auctionProcessRequest.setRemainingTime(Time.valueOf("04:15:00"));

        AuctionProcessResponse auctionProcessResponse = new AuctionProcessResponse();
        auctionProcessResponse.setCurrentBank(10000L);
        auctionProcessResponse.setRemainingTime(Time.valueOf("04:15:00"));

        session.send(String.format("/app/play/%d", testId), auctionProcessRequest);

        assertEquals(auctionProcessResponse, blockingQueue.poll(1, SECONDS));
    }
}
