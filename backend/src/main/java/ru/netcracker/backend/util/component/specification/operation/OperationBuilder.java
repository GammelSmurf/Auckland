package ru.netcracker.backend.util.component.specification.operation;

import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.util.component.specification.Filter;
import ru.netcracker.backend.util.component.specification.PredicateData;

import javax.persistence.criteria.*;

public abstract class OperationBuilder {
    private final Filter filter;
    private final PredicateData data;

    public OperationBuilder(Filter filter, PredicateData data) {
        this.filter = filter;
        this.data = data;
    }

    public abstract Predicate build();

    protected Root<Auction> getRoot() {
        return this.data.getRoot();
    }

    protected Path<Auction> get(String property) {
        return this.getRoot().get(property);
    }

    protected CriteriaQuery<?> getQuery() {
        return this.data.getQuery();
    }

    protected CriteriaBuilder getBuilder() {
        return this.data.getBuilder();
    }

    protected Filter getFilter() {
        return this.filter;
    }
}