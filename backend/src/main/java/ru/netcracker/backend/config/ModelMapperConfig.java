package ru.netcracker.backend.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.PropertyMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netcracker.backend.models.domain.Auction;
import ru.netcracker.backend.models.domain.Lot;
import ru.netcracker.backend.models.requests.AuctionRequest;
import ru.netcracker.backend.models.requests.LotRequest;
import ru.netcracker.backend.models.responses.AuctionResponse;
import ru.netcracker.backend.models.responses.LotResponse;

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
        PropertyMap<LotRequest, Lot> requestToLotMap = new PropertyMap<>() {
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
                map().setLots(source.getLots());
            }
        };

        PropertyMap<Lot, LotResponse> lotToResponseMap = new PropertyMap<>() {
            protected void configure() {
                map().setId(source.getId());
                map().setAuctionId(source.getAuction().getId());
            }
        };

        modelMapper.addMappings(requestToAuctionMap);
        modelMapper.addMappings(auctionToResponseMap);
        modelMapper.addMappings(lotToResponseMap);
        return modelMapper;
    }
}
