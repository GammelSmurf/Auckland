package ru.netcracker.backend.service.impl;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import ru.netcracker.backend.models.domain.Lot;
import ru.netcracker.backend.repository.LotRepo;
import ru.netcracker.backend.service.LotService;

@Service
public class LotServiceImpl implements LotService {
    private final LotRepo lotRepo;

    public LotServiceImpl(LotRepo lotRepo) {
        this.lotRepo = lotRepo;
    }

    @Override
    public Lot createLot(Lot lot) {
        return lotRepo.save(lot);
    }
}
