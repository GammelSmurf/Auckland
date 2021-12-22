package ru.netcracker.backend.util.component;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import ru.netcracker.backend.exception.lot.UserIsNotLotWinnerException;
import ru.netcracker.backend.exception.user.EmailExistsException;
import ru.netcracker.backend.model.entity.Lot;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.repository.LotRepository;
import ru.netcracker.backend.repository.UserRepository;

@Component
public class UserUtil {
    private final LotRepository lotRepository;

    @Autowired
    public UserUtil(LotRepository lotRepository) {
        this.lotRepository = lotRepository;
    }

    public void validateBeforeSigningUp(User user, UserRepository userRepository) {
        if (userRepository.existsByEmail(user.getEmail()))
            throw new EmailExistsException(user);
        if (userRepository.existsByUsername(user.getUsername()))
            throw new UsernameNotFoundException(user.getUsername());
    }

    public void validateBeforeGettingContactInfo(Long lotId) {
        lotRepository.findById(lotId).ifPresent(lot -> {
            if (!isWinnerOfLot(lot)) {
                throw new UserIsNotLotWinnerException(lot);
            }
        });
    }

    private boolean isWinnerOfLot(Lot lot) {
        return lot.getWinner().getUsername().equals(SecurityUtil.getUsernameFromSecurityCtx());
    }
}
