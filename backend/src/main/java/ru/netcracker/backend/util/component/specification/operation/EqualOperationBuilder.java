package ru.netcracker.backend.util.component.specification.operation;

import ru.netcracker.backend.model.entity.*;
import ru.netcracker.backend.util.SecurityUtil;
import ru.netcracker.backend.util.component.specification.Filter;
import ru.netcracker.backend.util.component.specification.PredicateData;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Path;
import javax.persistence.criteria.Predicate;


public class EqualOperationBuilder extends OperationBuilder {

    public EqualOperationBuilder(Filter filter, PredicateData data) {
        super(filter, data);
    }

    @Override
    public Predicate build() {
        switch (getFilter().getProperty()) {
            case "subscriber":
                return equals(getRoot().join(Auction_.subscribedUsers).get(User_.username), SecurityUtil.getUsernameFromSecurityCtx());
            case "creator":
                return equals(getRoot().join(Auction_.creator).get(User_.username), SecurityUtil.getUsernameFromSecurityCtx());
            case "other":
                return getBuilder().not(equals(getRoot().get(Auction_.status), AuctionStatus.DRAFT));
            default:
                return equalsWithOrPredicateOnValues();
        }
    }

    private Predicate equalsWithOrPredicateOnValues() {
        for (String value : getFilter().getValues()) {
            switch (getFilter().getProperty()) {
                case "status":
                    getPredicateList().add(equals(getRoot().get(getFilter().getProperty()), AuctionStatus.valueOf(value)));
                    break;
                case "tags":
                    getPredicateList().add(equals(getRoot().join(Auction_.tags).get(Tag_.name), value));
                    break;
                case "categories":
                    getPredicateList().add(equals(getRoot().join(Auction_.categories).get(Category_.name), value));
                    break;
                default:
                    getPredicateList().add(equals(getRoot().get(getFilter().getProperty()), value));
                    break;
            }
        }
        return getFilter().isOrPredicate()
                ? getBuilder().or(formatPredicateArray())
                : getBuilder().and(formatPredicateArray());
    }

    private Predicate equals(Expression<?> var1, Object var2) {
        return getBuilder().equal(var1, var2);
    }

    private Predicate equals(Path<?> var1, Object var2) {
        return getBuilder().equal(var1, var2);
    }
}
