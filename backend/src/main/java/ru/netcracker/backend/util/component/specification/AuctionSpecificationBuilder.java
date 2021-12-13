package ru.netcracker.backend.util.component.specification;

import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Auction_;
import ru.netcracker.backend.model.entity.Lot_;
import ru.netcracker.backend.model.requests.SearchRequest;
import ru.netcracker.backend.util.component.specification.operation.EqualOperationBuilder;
import ru.netcracker.backend.util.component.specification.operation.LikeOperationBuilder;
import ru.netcracker.backend.util.component.specification.operation.OperationBuilder;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public class AuctionSpecificationBuilder {
    private final SearchRequest searchRequest;
    private final List<javax.persistence.criteria.Predicate> filterPredicates = new ArrayList<>();
    private final List<Order> orderList = new ArrayList<>();
    private PredicateData data;

    public AuctionSpecificationBuilder(SearchRequest searchRequest) {
        this.searchRequest = searchRequest;
    }

    public javax.persistence.criteria.Predicate formatAuctionPredicates(Root<Auction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        copyPredicateData(root, query, criteriaBuilder);

        handleSortedPredicates();
        handleFilterPredicates();

        return criteriaBuilder.or(filterPredicates.toArray(new javax.persistence.criteria.Predicate[0]));
    }

    private void copyPredicateData(Root<Auction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        this.data = new PredicateData(root, query, builder);
    }

    private void handleSortedPredicates() {
        for (Sort sort : searchRequest.getSortList()) {
            orderList.add(generateOrder(sort));
        }
        data.getQuery()
                .groupBy(data.getRoot().get(Auction_.id))
                .orderBy(orderList);
    }

    private Order generateOrder(Sort sort) {
        switch (sort.getProperty()) {
            case "avgMinPrice":
                return formatAvgMinPriceOrder(sort);
            default:
                return formatOrder(sort);
        }
    }

    private Order formatAvgMinPriceOrder(Sort sort) {
        Expression<Double> expression = data.getBuilder().avg(data.getRoot().join(Auction_.lots).get(Lot_.minPrice));
        return sort.getDirection().isAscending()
                ? data.getBuilder().asc(expression)
                : data.getBuilder().desc(expression);
    }

    private Order formatOrder(Sort sort) {
        return sort.getDirection().isAscending()
                ? data.getBuilder().asc(data.getRoot().get(sort.getProperty()))
                : data.getBuilder().desc(data.getRoot().get(sort.getProperty()));
    }

    private void handleFilterPredicates() {
        for (Filter filter : searchRequest.getFilterList()) {
            addFilterToPredicateList(filter);
        }
    }

    private void addFilterToPredicateList(Filter filter) {
        filterPredicates.add(createOperationBuilder(filter).build());
    }

    private OperationBuilder createOperationBuilder(Filter filter) {
        switch (filter.getOperation()) {
            case EQUAL:
                return new EqualOperationBuilder(filter, data);
            case LIKE:
                return new LikeOperationBuilder(filter, data);
            default:
                throw new IllegalArgumentException("Operation is not correct");
        }
    }
}
