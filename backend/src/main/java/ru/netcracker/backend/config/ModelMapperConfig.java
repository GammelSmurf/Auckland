package ru.netcracker.backend.config;

import org.modelmapper.ModelMapper;
import org.modelmapper.TypeMap;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.requests.AuctionRequest;
import ru.netcracker.backend.requests.LotRequest;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.LotResponse;

@Configuration
public class ModelMapperConfig {

  @Bean
  public ModelMapper modelMapper() {
    ModelMapper modelMapper = new ModelMapper();
    modelMapper.getConfiguration().setFieldMatchingEnabled(true);

    TypeMap<AuctionRequest, Auction> requestToAuctionMap =
        modelMapper.createTypeMap(AuctionRequest.class, Auction.class);
    requestToAuctionMap.addMappings(mapper -> mapper.skip(Auction::setId));

    TypeMap<LotRequest, Lot> requestToLotMap =
        modelMapper.createTypeMap(LotRequest.class, Lot.class);
    requestToLotMap.addMappings(mapper -> mapper.skip(Lot::setId));

    TypeMap<Lot, LotResponse> lotToResponseMap =
        modelMapper.createTypeMap(Lot.class, LotResponse.class);
    lotToResponseMap.addMappings(
        mapper -> mapper.map(src -> src.getAuction().getId(), LotResponse::setAuctionId));

    TypeMap<Auction, AuctionResponse> auctionToResponseMap =
        modelMapper.createTypeMap(Auction.class, AuctionResponse.class);
    auctionToResponseMap.addMappings(
        mapper -> {
          mapper.map(Auction::getSubscribersCount, AuctionResponse::setUsersCount);
          mapper.map(Auction::getLikesCount, AuctionResponse::setUserLikes);
        });

    return modelMapper;
  }
}
