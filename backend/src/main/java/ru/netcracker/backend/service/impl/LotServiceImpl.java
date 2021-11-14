package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.responses.LotResponse;
import ru.netcracker.backend.service.LotService;
import ru.netcracker.backend.util.LotUtil;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class LotServiceImpl implements LotService {
    private final LotRepository lotRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public LotServiceImpl(LotRepository lotRepository, AuctionRepository auctionRepository, ModelMapper modelMapper) {
        this.lotRepository = lotRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotResponse> getAllLots() {
        return lotRepository.findAll().stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public LotResponse createLot(Lot lot) {
        return modelMapper.map(lotRepository.save(lot), LotResponse.class);
    }

    @Override
    public LotResponse updateLot(Long id, Lot lot) {
        Lot lotToUpdate = lotRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(LotUtil.LOT_NOT_FOUND_MSG_TEMPLATE, id)));
        lot.setId(lotToUpdate.getId());
        return modelMapper.map(lotRepository.save(lot), LotResponse.class);
    }

    @Override
    public void deleteLot(Long id) {
        lotRepository.deleteById(id);
    }

    @Override
    @Transactional(readOnly = true)
    public List<LotResponse> getLotsByAuctionId(Long auctionId) {
        return lotRepository.findAllByAuction_Id(auctionId).stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }
}
