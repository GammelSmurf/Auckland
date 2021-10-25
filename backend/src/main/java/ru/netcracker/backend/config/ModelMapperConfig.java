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

        PropertyMap<AuctionRequest, Auction> requestToAuctionMap = new PropertyMap<>() {
            protected void configure() {
                map().setId(null);
            }
        };

        PropertyMap<Auction, AuctionResponse> auctionToResponseMap = new PropertyMap<>() {
            protected void configure() {
                map().setId(source.getId());
                map().setUsersCount(source.getSubscribersCount());
                map().setUserLikes(source.getLikesCount());
                map().setUserId(source.getUser().getId());
                map().setBeginDate(source.getBeginDate());
                map().setBoostTime(source.getBoostTime());
                map().setLotDuration(source.getLotDuration());
            }
        };

        modelMapper.addMappings(requestToAuctionMap);
        modelMapper.addMappings(auctionToResponseMap);
        return modelMapper;
    }
}
