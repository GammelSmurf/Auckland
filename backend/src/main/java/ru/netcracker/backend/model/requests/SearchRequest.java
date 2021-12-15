package ru.netcracker.backend.model.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import ru.netcracker.backend.util.component.specification.Filter;
import ru.netcracker.backend.util.component.specification.Sort;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    private boolean orPredicate = false;
    private List<Sort> sortList;
    private List<Filter> filterList;
}
