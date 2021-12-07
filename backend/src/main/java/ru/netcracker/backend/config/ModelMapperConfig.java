package ru.netcracker.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import ru.netcracker.backend.model.*;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.requests.LotRequest;
import ru.netcracker.backend.requests.MessageRequest;
import ru.netcracker.backend.requests.TagRequest;
import ru.netcracker.backend.responses.*;

import java.time.Duration;
import java.time.LocalDateTime;

@Configuration
public class ModelMapperConfig {
    private ModelMapper modelMapper;
    private final UserRepository userRepository;

    @Autowired
    public ModelMapperConfig(UserRepository auctionRepository) {
        this.userRepository = auctionRepository;
    }

    @Bean
    public ModelMapper modelMapper() {
        modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);
        modelMapper.getConfiguration().setAmbiguityIgnored(true);
        auctionMapperConfiguration();
        return modelMapper;
    }

    private void auctionMapperConfiguration() {
        modelMapper.createTypeMap(AuctionRequest.class, Auction.class)
                .addMappings(
                        mapper -> {
                            mapper.skip(Auction::setId);
                            mapper.skip(Auction::setCreator);
                        })
                .setPostConverter(context -> {
                    context.getDestination().setCreator(
                            userRepository
                                    .findByUsername(context.getSource().getCreatorUsername())
                                    .orElseThrow(() -> new UsernameNotFoundException(context.getSource().getCreatorUsername())));
                    return context.getDestination();
                });

        modelMapper.createTypeMap(LotRequest.class, Lot.class)
                .addMappings(
                        mapper -> mapper.skip(Lot::setId));

        modelMapper.createTypeMap(Lot.class, LotResponse.class)
                .addMappings(
                        mapper -> mapper.map(src -> src.getAuction().getId(), LotResponse::setAuctionId));

        modelMapper.createTypeMap(Auction.class, AuctionResponse.class)
                .addMappings(
                        mapper -> {
                            mapper.map(Auction::getSubscribersCount, AuctionResponse::setUsersCount);
                            mapper.map(Auction::getLikesCount, AuctionResponse::setUserLikes);
                        });

        modelMapper.createTypeMap(MessageRequest.class, Message.class)
                .addMappings(
                        mapper -> mapper.skip(Message::setId))
                .setPostConverter(context -> {
                    context.getDestination().setSender(
                            userRepository
                                    .findByUsername(context.getSource().getSenderUsername())
                                    .orElseThrow(() -> new UsernameNotFoundException(context.getSource().getSenderUsername())));
                    return context.getDestination();

                });

        modelMapper.createTypeMap(Message.class, MessageResponse.class)
                .setPostConverter(context -> {
                    context.getDestination().setUsername(
                            context.getSource().getSender().getUsername());
                    return context.getDestination();
                });

        modelMapper.createTypeMap(Bet.class, BetResponse.class)
                .setPostConverter(context -> {
                    context.getDestination().setSecondsUntil(Math.abs(Duration.between(context.getSource().getLot().getEndTime(), LocalDateTime.now()).toSeconds()));
                    return context.getDestination();
                });

        modelMapper.createTypeMap(TagRequest.class, Tag.class)
                .addMappings(
                        mapper -> mapper.skip(Tag::setId));

        modelMapper.createTypeMap(Tag.class, TagResponse.class)
                .addMappings(
                        mapper -> {
                            mapper.map(src -> src.getAuction().getId(), TagResponse::setAuctionId);
                            mapper.map(src -> src.getCategory().getId(), TagResponse::setCategoryId);
                        });

        modelMapper.createTypeMap(Category.class, CategoryResponse.class);
    }
}
