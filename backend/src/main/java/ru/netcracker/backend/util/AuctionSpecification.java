package ru.netcracker.backend.util;

import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.model.*;
import ru.netcracker.backend.requests.SearchRequest;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Order;
import javax.persistence.criteria.Predicate;
import java.util.ArrayList;
import java.util.List;

@Component
public class AuctionSpecification {
    public Specification<Auction> getAuctions(String username, SearchRequest request) {
        return (root, query, builder) -> {
            List<Predicate> predicates = new ArrayList<>();

            if (isPresentAndNotEmpty(request.getKeyword())) {
                String[] keys = request.getKeyword().split("\\s+");
                for (String key : keys) {
                    if (key.startsWith("#")) {
                        predicates.add(builder.equal(builder.lower(root.join(Auction_.tags).get(Tag_.name)), formatTag(key)));
                    } else {
                        predicates.add(builder.like(builder.lower(builder.concat(root.get(Auction_.name), root.get(Auction_.description))),
                                "%" + key.toLowerCase() + "%"));
                    }
                }
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

            if (request.isSortedAvgPrice()) {
                Order order;
                Expression<Double> expression = builder.avg(root.join(Auction_.lots).get(Lot_.minPrice));
                if (request.isAsc()) {
                    order = builder.asc(expression);
                } else {
                    order = builder.desc(expression);
                }
                query.groupBy(root.get(Auction_.id)).orderBy(order);
            }

            return builder.and(predicates.toArray(new Predicate[0]));
        };
    }

    private String formatTag(String tag) {
        return tag.substring(1).toLowerCase();
    }

    private boolean isPresentAndNotEmpty(String str) {
        return str != null && !str.isEmpty();
    }
}
