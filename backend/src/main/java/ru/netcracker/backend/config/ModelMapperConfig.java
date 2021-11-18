package ru.netcracker.backend.config;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netcracker.backend.exception.user.UsernameNotFoundException;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.requests.LotRequest;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.LotResponse;

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
    }
}
