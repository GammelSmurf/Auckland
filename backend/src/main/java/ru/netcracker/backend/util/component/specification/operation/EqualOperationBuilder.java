package ru.netcracker.backend.util.component.specification.operation;

import ru.netcracker.backend.model.entity.*;
import ru.netcracker.backend.util.SecurityUtil;
import ru.netcracker.backend.util.component.specification.Filter;
import ru.netcracker.backend.util.component.specification.PredicateData;

import javax.persistence.criteria.*;


public class EqualOperationBuilder extends OperationBuilder {

    public EqualOperationBuilder(Filter filter, PredicateData data) {
        super(filter, data);
    }

    @Override
    public Predicate build() {
        switch (getFilter().getProperty()) {
            case "subscriber":
                return ifSubscribedPredicate();
            case "creator":
                return ifCreatorPredicate();
            case "other":
                return formatOtherPredicate();
            default:
                return equalsWithOrPredicateOnValues();
        }
    }

    private Predicate formatOtherPredicate() {
        return and(ifStatusDraft().not(), ifCreatorPredicate().not(), or(ifSubscribedPredicate().not(), ifSubscribedPredicateNull()));
    }

    private Predicate ifCreatorPredicate() {
        return equals(getRoot().join(Auction_.creator, JoinType.LEFT).get(User_.username), SecurityUtil.getUsernameFromSecurityCtx());
    }

    private Predicate ifStatusDraft() {
        return equals(getRoot().get(Auction_.status), AuctionStatus.DRAFT);
    }

    private Predicate ifSubscribedPredicate() {
        return equals(getJoinSubscribed(), SecurityUtil.getUsernameFromSecurityCtx());
    }

    private Predicate ifSubscribedPredicateNull() {
        return getJoinSubscribed().isNull();
    }

    private Path<String> getJoinSubscribed() {
        return getRoot().join(Auction_.subscribedUsers, JoinType.LEFT).get(User_.username);
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
}
