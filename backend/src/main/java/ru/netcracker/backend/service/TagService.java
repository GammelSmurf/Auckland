package ru.netcracker.backend.service;

import ru.netcracker.backend.model.entity.Tag;
import ru.netcracker.backend.model.responses.TagResponse;

import java.util.List;

public interface TagService {
    List<TagResponse> getTagsByAuctionId(Long auctionId);

    TagResponse createTag(Tag tag);

    void deleteTag(Long tagId);
}
