package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.model.responses.LotResponse;
import ru.netcracker.backend.service.LotService;
import ru.netcracker.backend.util.LotUtil;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LotServiceImpl implements LotService {
    private final LotRepository lotRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LotServiceImpl(LotRepository lotRepository, AuctionRepository auctionRepository, ModelMapper modelMapper) {
        this.lotRepository = lotRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<LotResponse> getAllLots() {
        return lotRepository.findAll().stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LotResponse createLot(Lot lot) {
        return modelMapper.map(lotRepository.save(lot), LotResponse.class);
    }

    @Override
    @Transactional
    public LotResponse updateLot(Long lotId, Lot newLot) {
        Lot oldLot = lotRepository
                .findById(lotId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(LotUtil.LOT_NOT_FOUND_MSG_TEMPLATE, lotId)));
        oldLot.setName(newLot.getName());
        oldLot.setDescription(newLot.getDescription());
        oldLot.setMinPrice(newLot.getMinPrice());
        oldLot.setPictureLink(newLot.getPictureLink());
        return modelMapper.map(lotRepository.save(oldLot), LotResponse.class);
    }

    @Override
    @Transactional
    public void deleteLot(Long lotId) {
        Optional<Lot> lotOptional = lotRepository.findById(lotId);
        if (lotOptional.isPresent()) {
            Lot lot = lotOptional.get();
            lot.getAuction().getLots().remove(lot);
            lotRepository.delete(lot);
        }
    }

    @Override
    public List<LotResponse> getLotsByAuctionId(Long auctionId) {
        return lotRepository.findAllByAuction_Id(auctionId).stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }
}
