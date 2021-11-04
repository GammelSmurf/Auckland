package ru.netcracker.backend.service;

import ru.netcracker.backend.model.Lot;

import java.util.List;

public interface LotService {
  List<Lot> getAllLots();

  Lot createLot(Lot lot);

  Lot updateLot(long id, Lot lot);

  void deleteLot(long id);

  Lot getLotById(long id);
}
