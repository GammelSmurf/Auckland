package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.model.Tag;
import ru.netcracker.backend.repository.TagRepository;
import ru.netcracker.backend.responses.TagResponse;
import ru.netcracker.backend.service.TagService;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class TagServiceImpl implements TagService {
    private final TagRepository tagRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public TagServiceImpl(TagRepository tagRepository, ModelMapper modelMapper) {
        this.tagRepository = tagRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public List<TagResponse> getTagsByAuctionId(Long auctionId) {
        return tagRepository.findAllByAuction_Id(auctionId).stream()
                .map(tag -> modelMapper.map(tag, TagResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public TagResponse createTag(Tag tag) {
        return modelMapper.map(tagRepository.save(tag), TagResponse.class);
    }

    @Override
    @Transactional
    public void deleteTag(Long id) {
        Optional<Tag> tagOptional = tagRepository.findById(id);
        if (tagOptional.isPresent()) {
            Tag tag = tagOptional.get();
            tag.getAuction().getTags().remove(tag);
            tag.getCategory().getTags().remove(tag);
            tagRepository.delete(tag);
        }
    }
}
