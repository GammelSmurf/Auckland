package ru.netcracker.backend.service.impl;

import org.springframework.stereotype.Service;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.service.LotService;

@Service
public class LotServiceImpl implements LotService {
  private final LotRepository lotRepository;

  public LotServiceImpl(LotRepository lotRepo) {
    this.lotRepository = lotRepo;
  }

  @Override
  public Lot createLot(Lot lot) {
    return lotRepository.save(lot);
  }
}
