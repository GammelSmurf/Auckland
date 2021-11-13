package ru.netcracker.backend.service;

import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.responses.LotResponse;

import java.util.List;

public interface LotService {
    List<LotResponse> getAllLots();

    LotResponse createLot(Lot lot);

    LotResponse updateLot(Long id, Lot lot);

    void deleteLot(Long id);

    List<LotResponse> getLotsByAuctionId(Long auctionId);
}
