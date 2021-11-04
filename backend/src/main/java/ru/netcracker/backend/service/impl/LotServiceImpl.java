package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.service.LotService;

import java.util.List;
import java.util.Optional;

@Service
@RequiredArgsConstructor
public class LotServiceImpl implements LotService {
  private final LotRepository lotRepository;

  @Override
  public List<Lot> getAllLots() {
    return lotRepository.findAll();
  }

  @Override
  public Lot createLot(Lot lot) {
    return lotRepository.save(lot);
  }

  @Override
  public Lot updateLot(long id, Lot lotRequest) {
    Lot lot =
        lotRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post id: " + id));

    lot.setName(lotRequest.getName());
    lot.setPicture(lotRequest.getPicture());
    lot.setDescription(lotRequest.getDescription());
    lot.setMinBank(lotRequest.getMinBank());
    lot.setStep(lotRequest.getStep());
    lot.setAuction(lotRequest.getAuction());

    return lotRepository.save(lot);
  }

  @Override
  public void deleteLot(long id) {
    Lot lot =
        lotRepository
            .findById(id)
            .orElseThrow(() -> new ResourceNotFoundException("Post id: " + id));

    lotRepository.delete(lot);
  }

  @Override
  public Lot getLotById(long id) {
    Optional<Lot> result = lotRepository.findById(id);
    if (result.isPresent()) {
      return result.get();
    } else {
      throw new ResourceNotFoundException("Post id: " + id);
    }
  }
}
