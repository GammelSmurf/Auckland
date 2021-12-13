package ru.netcracker.backend.util.component.specification;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.requests.SearchRequest;

@Component
public class AuctionSpecification {
    public Specification<Auction> getAuctionSpecification(SearchRequest searchRequest) {
        AuctionSpecificationBuilder builder = new AuctionSpecificationBuilder(searchRequest);
        return builder::formatAuctionPredicates;
    }
}
