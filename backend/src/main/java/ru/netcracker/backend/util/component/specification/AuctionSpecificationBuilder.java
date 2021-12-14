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

    public Predicate formatAuctionPredicates(Root<Auction> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        copyPredicateData(root, query, criteriaBuilder);

        handleSortedPredicates();
        handleFilterPredicates();

        return searchRequest.isOrPredicate()
                ? criteriaBuilder.or(formatPredicatesArray())
                : criteriaBuilder.and(formatPredicatesArray());
    }

    private Predicate[] formatPredicatesArray() {
        return filterPredicates.toArray(new Predicate[0]);
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
        return sort.getDirection().isAscending()
                ? data.getBuilder().asc(formatAvgExpression())
                : data.getBuilder().desc(formatAvgExpression());
    }

    private Expression<Double> formatAvgExpression() {
        return data.getBuilder().avg(data.getRoot().join(Auction_.lots).get(Lot_.minPrice));
    }

    private Order formatOrder(Sort sort) {
        return sort.getDirection().isAscending()
                ? data.getBuilder().asc(formatRootPathOfSortProperty(sort))
                : data.getBuilder().desc(formatRootPathOfSortProperty(sort));
    }

    private Expression<String> formatRootPathOfSortProperty(Sort sort) {
        return data.getRoot().get(sort.getProperty());
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
