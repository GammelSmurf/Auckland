package ru.netcracker.backend.util.component.specification;

import lombok.Data;
import ru.netcracker.backend.model.entity.Auction;

import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

@Data
public class PredicateData {
    private final Root<Auction> root;
    private final CriteriaQuery<?> query;
    private final CriteriaBuilder builder;

    public PredicateData(Root<Auction> root, CriteriaQuery<?> query, CriteriaBuilder builder) {
        this.root = root;
        this.query = query;

        this.builder = builder;
    }
}
