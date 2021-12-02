package ru.netcracker.backend.service;

import ru.netcracker.backend.model.Tag;
import ru.netcracker.backend.responses.TagResponse;

import java.util.List;

public interface TagService {
    List<TagResponse> getTagsByAuctionId(Long auctionId);

    TagResponse createTag(Tag tag);

    void deleteTag(Long id);
}
