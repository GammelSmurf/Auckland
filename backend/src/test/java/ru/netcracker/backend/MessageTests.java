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
import ru.netcracker.backend.model.Message;
import ru.netcracker.backend.requests.MessageRequest;
import ru.netcracker.backend.responses.BetResponse;
import ru.netcracker.backend.responses.MessageResponse;
import ru.netcracker.backend.service.AuctionService;
import ru.netcracker.backend.service.LotService;
import ru.netcracker.backend.service.MessageService;
import ru.netcracker.backend.service.UserService;

import java.lang.reflect.Type;
import java.time.LocalDateTime;
import java.util.List;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeoutException;

import static io.zonky.test.db.AutoConfigureEmbeddedDatabase.RefreshMode.AFTER_EACH_TEST_METHOD;
import static java.util.concurrent.TimeUnit.SECONDS;
import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(SpringExtension.class)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class MessageTests {
    @LocalServerPort
    private Integer port;

    @Autowired
    private ModelMapper modelMapper;

    @Autowired
    private MessageService messageService;

    @Autowired
    private AuctionService auctionService;

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
                        .get(1, SECONDS);
    }

    @Test
    public void testSendMessage() {
        MessageRequest messageRequest=new MessageRequest();
        messageRequest.setSenderUsername(TEST_USERNAME_1);
        messageRequest.setMessage("TEST_MESSAGE");
        messageRequest.setDateTimeMessage(LocalDateTime.now());
        messageRequest.setAuctionId(auctionService.getAuctionById(TEST_AUCTION_ID).getId());
        System.out.println("MessageRequest:"+messageRequest);


        MessageResponse messageResponse=messageService.addMessage(modelMapper.map(messageRequest, Message.class));
        System.out.println("Message Response:"+ messageResponse);

    }

    @Test
    public void testSentMessage()throws InterruptedException, ValidationException{
        ArrayBlockingQueue<MessageResponse> blockingStateQueue = new ArrayBlockingQueue<>(1);
        session.subscribe(
                String.format("/auction/chat/%d", TEST_AUCTION_ID),
                new StompFrameHandler() {
                    @Override
                    @NonNull
                    public Type getPayloadType(@NonNull StompHeaders headers) {
                        return MessageResponse.class;
                    }

                    @Override
                    public void handleFrame(@NonNull StompHeaders headers, Object playload) {
                        System.out.println("Received message: " + playload);
                        blockingStateQueue.add((MessageResponse) playload);
                    }
                });

        auctionService.subscribe(TEST_USERNAME_1, TEST_AUCTION_ID);

        MessageRequest messageRequest=new MessageRequest();
        messageRequest.setSenderUsername(TEST_USERNAME_1);
        messageRequest.setMessage("TEST_MESSAGE");
        messageRequest.setDateTimeMessage(LocalDateTime.now());
        messageRequest.setAuctionId(auctionService.getAuctionById(TEST_AUCTION_ID).getId());

        session.send(String.format("/app/send/%d", TEST_AUCTION_ID), messageRequest);

        Assertions.assertEquals("test",blockingStateQueue.poll(1,SECONDS).getUsername());
    }

    @Test
    public void testDeleteOldChats(){
        messageService.deleteOldChats();
    }
}
