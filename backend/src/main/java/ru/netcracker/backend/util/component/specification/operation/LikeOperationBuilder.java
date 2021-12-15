package ru.netcracker.backend.util.component.specification.operation;

import ru.netcracker.backend.util.component.specification.Filter;
import ru.netcracker.backend.util.component.specification.PredicateData;

import javax.persistence.criteria.Expression;
import javax.persistence.criteria.Predicate;

public class LikeOperationBuilder extends OperationBuilder{
    public LikeOperationBuilder(Filter filter, PredicateData data) {
        super(filter, data);
    }

    @Override
    public Predicate build() {
        for (String value : getFilter().getValues()) {
            getPredicateList().add(like(getConcatExpression(), generateValue(value)));
        }
        return getBuilder().or(getPredicateList().toArray(new Predicate[0]));
    }

    private Expression<String> getConcatExpression() {
        return getBuilder().lower(getRoot().get(getFilter().getProperty()));
    }

    private String generateValue(String value) {
        return "%" + value + "%";
    }

    private Predicate like(Expression<String> var1, String var2) {
        return getBuilder().like(var1, var2);
    }
}
