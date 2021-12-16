package ru.netcracker.backend.util.component.specification.operation;

import ru.netcracker.backend.model.entity.Auction_;
import ru.netcracker.backend.util.component.specification.Filter;
import ru.netcracker.backend.util.component.specification.PredicateData;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class LikeOperationBuilder extends OperationBuilder {
    public LikeOperationBuilder(Filter filter, PredicateData data) {
        super(filter, data);
    }

    @Override
    public Predicate build() {
        if (getFilter().getProperty().equals("nameAndDescription")) {
            addNameAndPropertyExpressionsToPredicateList();
        } else {
            addAnotherExpressionsToPredicateList();
        }
        return getFilter().isOrPredicate()
                ? getBuilder().or(formatPredicateArray())
                : getBuilder().and(formatPredicateArray());
    }

    private void addAnotherExpressionsToPredicateList() {
        for (String value : getFilter().getValues()) {
            getPredicateList().add(like(getConcatExpression(getFilter().getProperty()), generateValue(value)));
        }
    }

    private void addNameAndPropertyExpressionsToPredicateList() {
        for (String value : getFilter().getValues()) {
            getPredicateList().add(like(getRoot().get(Auction_.name), generateValue(value)));
            getPredicateList().add(like(getRoot().get(Auction_.description), generateValue(value)));
        }
    }

    private Expression<String> getConcatExpression(String property) {
        return getBuilder().lower(getRoot().get(property));
    }

    private String generateValue(String value) {
        return "%" + value + "%";
    }

    private Predicate like(Expression<String> var1, String var2) {
        return getBuilder().like(var1, var2);
    }
}
