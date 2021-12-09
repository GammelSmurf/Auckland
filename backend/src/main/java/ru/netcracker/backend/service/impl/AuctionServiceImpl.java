package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.AuctionAlreadyContainsCategoryException;
import ru.netcracker.backend.exception.auction.AuctionNotFoundException;
import ru.netcracker.backend.exception.auction.NotCorrectBeginDateException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.exception.category.CategoryNotFoundException;
import ru.netcracker.backend.exception.user.UsernameNotFoundException;
import ru.netcracker.backend.model.*;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.CategoryRepository;
import ru.netcracker.backend.repository.TagRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.requests.SearchRequest;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.CategoryResponse;
import ru.netcracker.backend.responses.UserResponse;
import ru.netcracker.backend.service.AuctionService;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.service.TagService;
import ru.netcracker.backend.util.AuctionSpecification;
import ru.netcracker.backend.util.LogLevel;
import ru.netcracker.backend.util.NotificationLevel;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class AuctionServiceImpl implements AuctionService {
    private final AuctionRepository auctionRepository;
    private final UserRepository userRepository;
    private final CategoryRepository categoryRepository;
    private final TagRepository tagRepository;
    private final LogService logService;
    private final ModelMapper modelMapper;
    private final AuctionSpecification auctionSpecification;
    private final NotificationService notificationService;

    @Autowired
    public AuctionServiceImpl(AuctionRepository auctionRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              TagRepository tagRepository,
                              LogService logService,
                              TagService tagService,
                              ModelMapper modelMapper, AuctionSpecification auctionSpecification, NotificationService notificationService) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.logService = logService;
        this.modelMapper = modelMapper;
        this.auctionSpecification = auctionSpecification;
        this.notificationService = notificationService;
    }

    @Override
    public List<AuctionResponse> getAllAuctions(Pageable pageable) {
        return auctionRepository
                .findAll(pageable).stream()
                .map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuctionResponse> searchAuctions(String username, SearchRequest searchRequest, Pageable pageable) {
        return auctionRepository
                .findAll(auctionSpecification.getAuctions(username, searchRequest), pageable).stream()
                .map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public AuctionResponse createAuction(Auction auction) {
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }

    @Override
    @Transactional
    public AuctionResponse updateAuction(Long auctionId, Auction newAuction) {
        Auction oldAuction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        oldAuction.setName(newAuction.getName());
        oldAuction.setDescription(newAuction.getDescription());
        oldAuction.setUsersNumberLimit(newAuction.getUsersNumberLimit());
        oldAuction.setBeginDate(newAuction.getBeginDate());
        oldAuction.setLotDuration(newAuction.getLotDuration());
        oldAuction.setBoostTime(newAuction.getBoostTime());
        return modelMapper.map(auctionRepository.save(oldAuction), AuctionResponse.class);
    }

    @Override
    @Transactional
    public void deleteAuction(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        auctionRepository.delete(auction);
    }

    @Override
    public AuctionResponse getAuctionById(Long auctionId) {
        return modelMapper.map(
                auctionRepository
                        .findById(auctionId)
                        .orElseThrow(() -> new AuctionNotFoundException(auctionId)),
                AuctionResponse.class);
    }

    @Override
    @Transactional
    public void makeAuctionWaitingWithAnotherLot(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        if (!auction.isDraft()) {
            throw new NotCorrectStatusException(auction);
        }

        LocalDateTime currentDate = LocalDateTime.now();
        if (currentDate.isAfter(auction.getBeginDate())) {
            throw new NotCorrectBeginDateException();
        }
        auction.setStatus(AuctionStatus.WAITING);
        auction.setAnotherLot();
        logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
    }

    @Override
    @Transactional
    public UserResponse subscribe(String username, Long auctionId) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));

        notificationService.log(NotificationLevel.USER_SUBSCRIBED, user, auction);

        auction.getSubscribers().add(user);
        user.getSubscribedAuctions().add(auction);
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    @Transactional
    public CategoryResponse addCategoryToAuction(Long auctionId, Long categoryId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        if (auction.getCategories().contains(category)) {
            throw new AuctionAlreadyContainsCategoryException(category);
        }
        auction.getCategories().add(category);
        category.getAuctions().add(auction);
        auctionRepository.save(auction);
        return modelMapper.map(category, CategoryResponse.class);
    }

    @Override
    @Transactional
    public AuctionResponse removeCategoryFromAuction(Long auctionId, Long categoryId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new CategoryNotFoundException(categoryId));

        List<Tag> tags = tagRepository.findAllByAuction_IdAndCategory_Id(auctionId, categoryId);
        tags.forEach(auction.getTags()::remove);
        tags.forEach(category.getTags()::remove);

        auction.getCategories().remove(category);
        category.getAuctions().remove(auction);
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }
}
