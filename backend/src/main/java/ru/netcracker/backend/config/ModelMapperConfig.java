package ru.netcracker.backend.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.models.requests.AuctionRequest;
import ru.netcracker.backend.models.responses.AuctionResponse;

@Configuration
public class ModelMapperConfig {
    @Bean
    public ModelMapper modelMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration().setFieldMatchingEnabled(true);

        PropertyMap<Auction, AuctionRequest> auctionMap = new PropertyMap<>() {
            protected void configure() {
                map().setUserId(source.getUser().getId());
            }
        };

        PropertyMap<AuctionRequest, Auction> auctionRequestMap = new PropertyMap<>() {
            protected void configure() {
                map().setId(null);
            }
        };

        PropertyMap<Auction, AuctionResponse> auctionResponseMap = new PropertyMap<>() {
            protected void configure() {
                map().setUsersCount(source.getSubscribersCount());
                map().setUserLikes(source.getLikesCount());
                map().setUserId(source.getUser().getId());
            }
        };


        modelMapper.addMappings(auctionMap);
        modelMapper.addMappings(auctionRequestMap);
        modelMapper.addMappings(auctionResponseMap);
        return modelMapper;
    }
}
