package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Pageable;
import org.springframework.data.rest.webmvc.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.AuctionAlreadyContainsCategoryException;
import ru.netcracker.backend.exception.auction.NoLotsException;
import ru.netcracker.backend.exception.auction.NotCorrectBeginDateException;
import ru.netcracker.backend.exception.auction.NotCorrectStatusException;
import ru.netcracker.backend.exception.user.UsernameNotFoundException;
import ru.netcracker.backend.model.*;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.CategoryRepository;
import ru.netcracker.backend.repository.TagRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.responses.AuctionResponse;
import ru.netcracker.backend.responses.CategoryResponse;
import ru.netcracker.backend.responses.UserResponse;
import ru.netcracker.backend.service.AuctionService;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.service.TagService;
import ru.netcracker.backend.util.AuctionUtil;
import ru.netcracker.backend.util.LogLevel;
import ru.netcracker.backend.util.NotificationLevel;
import ru.netcracker.backend.util.UserUtil;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
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
    private final NotificationService notificationService;

    @Autowired
    public AuctionServiceImpl(AuctionRepository auctionRepository, UserRepository userRepository, CategoryRepository categoryRepository, TagRepository tagRepository, LogService logService, TagService tagService, ModelMapper modelMapper, NotificationService notificationService) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.logService = logService;
        this.modelMapper = modelMapper;
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
    public List<AuctionResponse> getAllSubscribedAuctions(String username, Pageable pageable) {
        return auctionRepository
                .findBySubscribers_Username(username, pageable).stream()
                .map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuctionResponse> getAllOwnAuctions(String username, Pageable pageable) {
        return auctionRepository
                .findByCreator_Username(username, pageable).stream()
                .map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public List<AuctionResponse> searchAuctions(String keyword, Pageable pageable) {
        return auctionRepository
                .search(keyword, pageable).stream()
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
    public AuctionResponse updateAuction(Long id, Auction newAuction) {
        Auction oldAuction = auctionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)));
        oldAuction.setName(newAuction.getName());
        oldAuction.setDescription(newAuction.getDescription());
        oldAuction.setUsersLimit(newAuction.getUsersLimit());
        oldAuction.setBeginDate(newAuction.getBeginDate());
        oldAuction.setLotDuration(newAuction.getLotDuration());
        oldAuction.setBoostTime(newAuction.getBoostTime());
        return modelMapper.map(auctionRepository.save(oldAuction), AuctionResponse.class);
    }

    @Override
    @Transactional
    public void deleteAuction(Long id) {
        Auction auction = auctionRepository
                .findById(id)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id)));
        auctionRepository.delete(auction);
    }

    @Override
    public AuctionResponse getAuctionById(Long id) {
        return modelMapper.map(
                auctionRepository
                        .findById(id)
                        .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, id))),
                AuctionResponse.class);
    }

    @Override
    @Transactional
    public void makeAuctionWaiting(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        LocalDateTime currentDate = LocalDateTime.now();
        switch (auction.getStatus()) {
            case DRAFT:
                Optional<Lot> lotOptional = AuctionUtil.getAnotherLot(auction);
                if (currentDate.isAfter(auction.getBeginDate())) {
                    throw new NotCorrectBeginDateException();
                }
                if (lotOptional.isPresent()) {
                    auction.setStatus(AuctionStatus.WAITING);
                    auction.setCurrentLot(lotOptional.get());
                    logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
                } else {
                    throw new NoLotsException();
                }
                break;
            case WAITING:
            case RUNNING:
            case FINISHED:
            default:
                throw new NotCorrectStatusException(auction);
        }
    }

    @Override
    @Transactional
    public UserResponse subscribe(String username, Long auctionId) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format(UserUtil.USER_NOT_FOUND_TEMPLATE, username)));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));

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
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.CATEGORY_NOT_FOUND_TEMPLATE, categoryId)));

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
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.AUCTION_NOT_FOUND_TEMPLATE, auctionId)));
        Category category = categoryRepository
                .findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(String.format(AuctionUtil.CATEGORY_NOT_FOUND_TEMPLATE, categoryId)));

        List<Tag> tags = tagRepository.findAllByAuction_IdAndCategory_Id(auctionId, categoryId);
        tags.forEach(auction.getTags()::remove);
        tags.forEach(category.getTags()::remove);

        auction.getCategories().remove(category);
        category.getAuctions().remove(auction);
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }
}
