package ru.netcracker.backend.util.component.specification.operation;

import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.util.component.specification.Filter;
import ru.netcracker.backend.util.component.specification.PredicateData;

import javax.persistence.criteria.*;
import java.util.ArrayList;
import java.util.List;

public abstract class OperationBuilder {
    private final Filter filter;
    private final PredicateData data;
    private List<Predicate> predicateList = new ArrayList<>();

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

    protected List<Predicate> getPredicateList() {
        return predicateList;
    }

    protected Predicate[] formatPredicateArray() {
        return getPredicateList().toArray(new Predicate[0]);
    }

    protected Predicate and(Predicate var1, Predicate var2) {
        return getBuilder().and(var1, var2);
    }

    protected Predicate and(Predicate var1, Predicate var2, Predicate var3) {
        return getBuilder().and(var1, var2, var3);
    }

    protected Predicate or(Predicate var1, Predicate var2) {
        return getBuilder().or(var1, var2);
    }

    protected Predicate equals(Path<?> var1, Object var2) {
        return getBuilder().equal(var1, var2);
    }
}