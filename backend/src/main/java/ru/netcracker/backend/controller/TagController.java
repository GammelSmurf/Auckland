package ru.netcracker.backend.controller;

import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.netcracker.backend.model.Tag;
import ru.netcracker.backend.requests.TagRequest;
import ru.netcracker.backend.responses.TagResponse;
import ru.netcracker.backend.service.TagService;

import javax.validation.Valid;
import java.util.List;

@RestController
@RequestMapping("/api/tag")
@CrossOrigin("*")
@Slf4j
@Validated
public class TagController {
    private final TagService tagService;
    private final ModelMapper modelMapper;

    @Autowired
    public TagController(TagService tagService, ModelMapper modelMapper) {
        this.tagService = tagService;
        this.modelMapper = modelMapper;
    }

    @GetMapping("/{id}")
    public List<TagResponse> getTagsByAuctionId(@PathVariable(name = "id") Long id) {
        return tagService.getTagsByAuctionId(id);
    }

    @PostMapping
    public ResponseEntity<TagResponse> createTag(@Valid @RequestBody TagRequest tagRequest) {
        TagResponse tagResponse = tagService.createTag(
                modelMapper.map(tagRequest, Tag.class));

        log.info("created tag: {}", tagRequest);
        return new ResponseEntity<>(tagResponse, HttpStatus.CREATED);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable(name = "id") Long id) {
        tagService.deleteTag(id);
        log.info("deleted tag with id: {}", id);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
