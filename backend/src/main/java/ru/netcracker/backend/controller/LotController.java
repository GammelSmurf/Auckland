package ru.netcracker.backend.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.model.Lot;
import ru.netcracker.backend.requests.LotRequest;
import ru.netcracker.backend.responses.LotResponse;
import ru.netcracker.backend.service.LotService;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/lot")
@CrossOrigin("*")
@RequiredArgsConstructor
@Slf4j
public class LotController {
    private final ModelMapper modelMapper;
    private final LotService lotService;

    @GetMapping
    public List<LotResponse> getAllLots() {
        return lotService.getAllLots().stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }

    @GetMapping("/{id}")
    public ResponseEntity<LotResponse> getLot(@PathVariable(name = "id") Long id) {
        LotResponse lotDto = modelMapper.map(lotService.getLotById(id), LotResponse.class);

        log.info("sent lot: {}", lotDto);
        return new ResponseEntity<>(lotDto, HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<LotResponse> createLot(@RequestBody LotRequest lotDto) {
        LotResponse lotResponse =
                modelMapper.map(
                        lotService.createLot(modelMapper.map(lotDto, Lot.class)),
                        LotResponse.class);

        log.info("created lot: {}", lotDto);
        return new ResponseEntity<>(lotResponse, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<LotResponse> updateLot(
            @PathVariable long id, @RequestBody LotRequest lotDto) {
        LotResponse lotResponse =
                modelMapper.map(
                        lotService.updateLot(id, modelMapper.map(lotDto, Lot.class)),
                        LotResponse.class);

        log.info("updated lot: {} with id: {}", lotDto, id);
        return new ResponseEntity<>(lotResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteLot(@PathVariable(name = "id") Long id) {
        lotService.deleteLot(id);
        log.info("deleted lot with id: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
