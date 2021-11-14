package ru.netcracker.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.requests.LotRequest;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.LotResponse;

@Configuration
public class ModelMapperConfig {
    private ModelMapper modelMapper;

    @Bean
    public ModelMapper modelMapper() {
        if (modelMapper == null) {
            modelMapper = new ModelMapper();
            modelMapper.getConfiguration().setFieldMatchingEnabled(true);
            auctionMapperConfiguration();
        }
        return modelMapper;
    }

    private void auctionMapperConfiguration() {
        modelMapper.createTypeMap(AuctionRequest.class, Auction.class)
                .addMappings(
                        mapper -> {
                            mapper.skip(Auction::setId);
                            mapper.skip(Auction::setCreator);
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
    }
}
