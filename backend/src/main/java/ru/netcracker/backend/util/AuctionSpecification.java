package ru.netcracker.backend.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.model.Auction;
import ru.netcracker.backend.model.AuctionStatus;
import ru.netcracker.backend.model.Auction_;
import ru.netcracker.backend.model.User_;
import ru.netcracker.backend.requests.SearchRequest;

import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuctionSpecification {
    public Specification<Auction> getAuctions(String username, SearchRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (isPresentAndNotEmpty(request.getKeyword())) {
                predicates.add(builder.like(builder.lower(builder.concat(root.get(Auction_.name), root.get(Auction_.description))),
                        "%" + request.getKeyword().toLowerCase() + "%"));
            }
            if (request.isSubscriptions()) {
                predicates.add(builder.equal(root.join(Auction_.subscribers).get(User_.username), username));
            }
            if (request.isOwnAuctions()) {
                predicates.add(builder.equal(root.join(Auction_.creator).get(User_.username), username));
            }
            if (request.isWaiting()) {
                predicates.add(builder.equal(root.get(Auction_.status), AuctionStatus.WAITING));
            }
            if (request.isRunning()) {
                predicates.add(builder.equal(root.get(Auction_.status), AuctionStatus.RUNNING));
            }
            if (request.isFinished()) {
                predicates.add(builder.equal(root.get(Auction_.status), AuctionStatus.FINISHED));
            }
            for (String categoryStr : request.getCategories()) {
                predicates.add(builder.equal(root.get(Auction_.categories), categoryStr));
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }


    private boolean isPresentAndNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
