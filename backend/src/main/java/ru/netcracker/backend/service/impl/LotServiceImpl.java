package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.lot.LotNotFoundException;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.model.entity.Transaction;
import ru.netcracker.backend.model.entity.TransactionStatus;
import ru.netcracker.backend.model.responses.LotResponse;
import ru.netcracker.backend.model.responses.LotTransferredAndNotResponse;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.repository.TransactionRepository;
import ru.netcracker.backend.service.LotService;
import ru.netcracker.backend.service.UserService;
import ru.netcracker.backend.util.component.LotUtil;
import ru.netcracker.backend.util.component.SecurityUtil;
import ru.netcracker.backend.util.component.RandomNameGenerator;
import ru.netcracker.backend.util.component.email.EmailSender;

import javax.mail.MessagingException;
import java.io.UnsupportedEncodingException;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class LotServiceImpl implements LotService {
    private final LotRepository lotRepository;
    private final TransactionRepository transactionRepository;
    private final EmailSender emailSender;
    private final ModelMapper modelMapper;
    private final LotUtil lotUtil;
    private final UserService userService;
    private final RandomNameGenerator randomNameGenerator;

    @Autowired
    public LotServiceImpl(LotRepository lotRepository,
                          TransactionRepository transactionRepository,
                          EmailSender emailSender,
                          ModelMapper modelMapper,
                          LotUtil lotUtil,
                          UserService userService,
                          RandomNameGenerator randomNameGenerator) {
        this.lotRepository = lotRepository;
        this.transactionRepository = transactionRepository;
        this.emailSender = emailSender;
        this.modelMapper = modelMapper;
        this.lotUtil = lotUtil;
        this.userService = userService;
        this.randomNameGenerator = randomNameGenerator;
    }

    @Override
    public List<LotResponse> getAllLots() {
        return lotRepository.findAll().stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LotResponse createLot(Lot lot) {
        lotUtil.validateBeforeCreating(lot);
        setRandomNameIfNull(lot);
        return modelMapper.map(lotRepository.save(lot), LotResponse.class);
    }

    private void setRandomNameIfNull(Lot lot) {
        if (lot.getName() == null) {
            lot.setName(randomNameGenerator.getName(7));
        }
    }

    @Override
    @Transactional
    public LotResponse updateLot(Long lotId, Lot newLot) {
        lotUtil.validateBeforeUpdating(newLot);
        Lot oldLot = lotRepository
                .findById(lotId)
                .orElseThrow(() -> new LotNotFoundException(lotId));
        oldLot.copyMainParamsFrom(newLot);
        return modelMapper.map(lotRepository.save(oldLot), LotResponse.class);
    }

    @Override
    @Transactional
    public void deleteLot(Long lotId) {
        Lot lot = lotRepository
                .findById(lotId)
                .orElseThrow(() -> new LotNotFoundException(lotId));
        lotUtil.validateBeforeDeleting(lot);
        lot.getAuction().getLots().remove(lot);
        lotRepository.delete(lot);
    }

    @Override
    public List<LotResponse> getLotsByAuctionId(Long auctionId) {
        return lotRepository.findAllByAuction_Id(auctionId).stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    public LotTransferredAndNotResponse getLotsTransferredAndNot() {
        return new LotTransferredAndNotResponse(
                getLotsResponseByTransferred(true),
                getLotsResponseByTransferred(false)
        );
    }

    private List<LotResponse> getLotsResponseByTransferred(boolean transferred) {
        return lotRepository.findAllIfWinnerOrCreatorByTransferred(SecurityUtil.getUsernameFromSecurityCtx(), transferred).stream()
                .map(lot -> modelMapper.map(lot, LotResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public LotResponse confirmLotTransfer(Long lotId) {
        Lot lot = lotRepository
                .findById(lotId)
                .orElseThrow(() -> new LotNotFoundException(lotId));
        lotUtil.validateBeforeConfirmationLotTransfer(lot);
        lot.confirmSellerTransfer();
        makeTransactionDoneIfTransferredAndGiveMoneyToSeller(lot);
        return modelMapper.map(lotRepository.save(lot), LotResponse.class);
    }

    @Override
    @Transactional
    public LotResponse confirmLotAcceptance(Long lotId) {
        Lot lot = lotRepository
                .findById(lotId)
                .orElseThrow(() -> new LotNotFoundException(lotId));
        lotUtil.validateBeforeConfirmationLotAccept(lot);
        lot.confirmBuyerAccept();
        makeTransactionDoneIfTransferredAndGiveMoneyToSeller(lot);
        return modelMapper.map(lotRepository.save(lot), LotResponse.class);
    }

    private void makeTransactionDoneIfTransferredAndGiveMoneyToSeller(Lot lot) {
        if (lot.isTransferred()) {
            transactionRepository.findByLot(lot).ifPresent(tx -> {
                tx.setTransactionStatus(TransactionStatus.DONE);
                lot.getAuction().getCreator().addMoney(tx.getAmount());
                transactionRepository.save(tx);
                userService.sendMoneyToWsByUser(lot.getAuction().getCreator());
                sendTransferredStatusEmails(tx);
            });
        }
    }

    private void sendTransferredStatusEmails(Transaction tx) {
        try {
            emailSender.createAndSendBuyerTransactionDoneEmail(tx.getBuyer());
            emailSender.createAndSendSellerTransactionDoneEmail(tx.getAuctionCreator(), tx);
        } catch (MessagingException | UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }
}
