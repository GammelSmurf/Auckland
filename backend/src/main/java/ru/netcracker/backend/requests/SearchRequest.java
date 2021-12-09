package ru.netcracker.backend.requests;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchRequest {
    private String keyword;
    private boolean ownAuctions;
    private boolean subscriptions;
    private boolean waiting;
    private boolean running;
    private boolean finished;
    private boolean sortedAvgPrice;
    private boolean asc;
    private List<String> categories;
}
