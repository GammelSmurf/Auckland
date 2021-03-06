package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.auction.AuctionNotFoundException;
import ru.netcracker.backend.exception.category.CategoryNotFoundException;
import ru.netcracker.backend.exception.user.UsernameNotFoundException;
import ru.netcracker.backend.model.entity.Auction;
import ru.netcracker.backend.model.entity.Category;
import ru.netcracker.backend.model.entity.Tag;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.requests.SearchRequest;
import ru.netcracker.backend.model.responses.AuctionResponse;
import ru.netcracker.backend.model.responses.CategoryResponse;
import ru.netcracker.backend.model.responses.UserResponse;
import ru.netcracker.backend.repository.AuctionRepository;
import ru.netcracker.backend.repository.CategoryRepository;
import ru.netcracker.backend.repository.TagRepository;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.service.AuctionService;
import ru.netcracker.backend.service.LogService;
import ru.netcracker.backend.service.NotificationService;
import ru.netcracker.backend.util.component.SecurityUtil;
import ru.netcracker.backend.util.component.AuctionUtil;
import ru.netcracker.backend.util.component.RandomNameGenerator;
import ru.netcracker.backend.util.component.specification.AuctionSpecification;
import ru.netcracker.backend.util.enumiration.LogLevel;
import ru.netcracker.backend.util.enumiration.NotificationLevel;

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
    private final RandomNameGenerator nameGenerator;
    private final AuctionUtil auctionUtil;

    @Autowired
    public AuctionServiceImpl(AuctionRepository auctionRepository,
                              UserRepository userRepository,
                              CategoryRepository categoryRepository,
                              TagRepository tagRepository,
                              LogService logService,
                              ModelMapper modelMapper,
                              AuctionSpecification auctionSpecification,
                              NotificationService notificationService,
                              RandomNameGenerator nameGenerator,
                              AuctionUtil auctionUtil) {
        this.auctionRepository = auctionRepository;
        this.userRepository = userRepository;
        this.categoryRepository = categoryRepository;
        this.tagRepository = tagRepository;
        this.logService = logService;
        this.modelMapper = modelMapper;
        this.auctionSpecification = auctionSpecification;
        this.notificationService = notificationService;
        this.nameGenerator = nameGenerator;
        this.auctionUtil = auctionUtil;
    }

    @Override
    public Page<AuctionResponse> getAllAuctions(Pageable pageable) {
        return auctionRepository
                .findAll(pageable)
                .map(auction -> modelMapper.map(auction, AuctionResponse.class));
    }

    @Override
    public List<AuctionResponse> getAllIfCreator() {
        return auctionRepository
                .findAllByCreator_Username(SecurityUtil.getUsernameFromSecurityCtx()).stream()
                .map(auction -> modelMapper.map(auction, AuctionResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public Page<AuctionResponse> searchAuctions(SearchRequest searchRequest, int page, int size) {
        return auctionRepository
                .findAll(auctionSpecification.getAuctionSpecification(searchRequest), PageRequest.of(page, size))
                .map(auction -> modelMapper.map(auction, AuctionResponse.class));
    }

    @Override
    @Transactional
    public AuctionResponse createAuction(Auction auction) {
        auctionUtil.validateBeforeCreating(auction);
        setRandomName(auction);
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }

    private void setRandomName(Auction auction) {
        auction.setName(nameGenerator.getName(7));
    }

    @Override
    @Transactional
    public AuctionResponse updateAuction(Long auctionId, Auction newAuction) {
        Auction oldAuction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        auctionUtil.validateBeforeUpdating(oldAuction, newAuction);
        oldAuction.copyMainParamsFrom(newAuction);
        return modelMapper.map(auctionRepository.save(oldAuction), AuctionResponse.class);
    }

    @Override
    @Transactional
    public void deleteAuction(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        auctionUtil.validateBeforeDeleting(auction);
        auctionRepository.delete(auction);
    }

    @Override
    public AuctionResponse getAuctionById(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        auctionUtil.validateBeforeGetting(auction);
        return modelMapper.map(auction ,AuctionResponse.class);
    }

    @Override
    @Transactional
    public void makeAuctionWaitingWithAnotherLot(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        auctionUtil.validateBeforeMakingWaiting(auction);
        auction.setWaitingStatus();
        auction.setAnotherLot();
        logService.log(LogLevel.AUCTION_STATUS_CHANGE, auctionRepository.save(auction));
    }

    @Override
    @Transactional
    public UserResponse subscribe(Long auctionId) {
        User user = userRepository
                .findByUsername(SecurityUtil.getUsernameFromSecurityCtx())
                .orElseThrow(() -> new UsernameNotFoundException(SecurityUtil.getUsernameFromSecurityCtx()));
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        auctionUtil.validateBeforeSubscribing(auction);
        user.subscribeToAuction(auction);
        notificationService.log(NotificationLevel.USER_SUBSCRIBED, userRepository.save(user), auction);
        return modelMapper.map(user, UserResponse.class);
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
        auctionUtil.validateBeforeAddingCategoryToAuction(auction, category);
        auction.addCategory(category);
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
        removeTagConnectionsFromCategoryAndAuction(auction, category);
        auction.removeCategory(category);
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }

    private void removeTagConnectionsFromCategoryAndAuction(Auction auction, Category category) {
        List<Tag> tags = tagRepository.findAllByAuction_IdAndCategory_Id(auction.getId(), category.getId());
        tags.forEach(auction.getTags()::remove);
        tags.forEach(category.getTags()::remove);
    }

    @Override
    @Transactional
    public AuctionResponse addLike(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        User user = userRepository
                .findByUsername(SecurityUtil.getUsernameFromSecurityCtx())
                .orElseThrow(() -> new UsernameNotFoundException(SecurityUtil.getUsernameFromSecurityCtx()));
        auctionUtil.validateBeforeAddingLike(auction, user);
        auction.addUserWhoLikedAndIncreaseLikesCount(user);
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }

    @Override
    @Transactional
    public AuctionResponse removeLike(Long auctionId) {
        Auction auction = auctionRepository
                .findById(auctionId)
                .orElseThrow(() -> new AuctionNotFoundException(auctionId));
        User user = userRepository
                .findByUsername(SecurityUtil.getUsernameFromSecurityCtx())
                .orElseThrow(() -> new UsernameNotFoundException(SecurityUtil.getUsernameFromSecurityCtx()));
        auctionUtil.validateBeforeDislike(auction, user);
        auction.removeUserWhoLikedAndDecreaseLikesCount(user);
        return modelMapper.map(auctionRepository.save(auction), AuctionResponse.class);
    }
}
