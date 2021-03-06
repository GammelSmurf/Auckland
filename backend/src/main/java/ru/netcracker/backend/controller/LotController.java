package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.model.requests.LotRequest;
import ru.netcracker.backend.model.responses.LotResponse;
import ru.netcracker.backend.model.responses.LotTransferredAndNotResponse;
import ru.netcracker.backend.service.LotService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/lot")
@CrossOrigin("*")
@Slf4j
@Validated
public class LotController {
    private final LotService lotService;
    private final ModelMapper modelMapper;

    public LotController(LotService lotService, ModelMapper modelMapper) {
        this.lotService = lotService;
        this.modelMapper = modelMapper;
    }

    @GetMapping
    public List<LotResponse> getAllLots() {
        return lotService.getAllLots();
    }

    @GetMapping("/{id}")
    public List<LotResponse> getLotsByAuctionId(@PathVariable(name = "id") Long id) {
        return lotService.getLotsByAuctionId(id);
    }

    @GetMapping("/won")
    public LotTransferredAndNotResponse getLotsWonAndTransferred() {
        return lotService.getLotsTransferredAndNot();
    }

    @PostMapping("/confirm/transfer/{lotId}")
    public LotResponse confirmLotTransfer(@PathVariable(name = "lotId") Long lotId) {
        return lotService.confirmLotTransfer(lotId);
    }

    @PostMapping("/confirm/accept/{lotId}")
    public LotResponse confirmLotAcceptance(@PathVariable(name = "lotId") Long lotId) {
        return lotService.confirmLotAcceptance(lotId);
    }

    @PostMapping
    public ResponseEntity<LotResponse> createLot(@Valid @RequestBody LotRequest lotRequest) {
        LotResponse lotResponse = lotService.createLot(modelMapper.map(lotRequest, Lot.class));
        log.info("created lot: {}", lotRequest);
        return ResponseEntity.ok(lotResponse);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotResponse> updateLot(
            @PathVariable long id, @Valid @RequestBody LotRequest lotRequest) {
        LotResponse lotResponse = lotService.updateLot(id, modelMapper.map(lotRequest, Lot.class));
        log.info("updated lot: {} with id: {}", lotRequest, id);
        return ResponseEntity.ok(lotResponse);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLot(@PathVariable(name = "id") Long id) {
        lotService.deleteLot(id);
        log.info("deleted lot with id: {}", id);
        return ResponseEntity.ok().build();
    }
}
