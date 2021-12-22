package ru.netcracker.backend.service.impl;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.netcracker.backend.exception.lot.LotNotFoundException;
import ru.netcracker.backend.model.entity.User;
import ru.netcracker.backend.model.requests.UserRequest;
import ru.netcracker.backend.model.responses.ContactInfoResponse;
import ru.netcracker.backend.model.responses.UserResponse;
import ru.netcracker.backend.repository.UserRepository;
import ru.netcracker.backend.service.UserService;
import ru.netcracker.backend.util.component.UserUtil;

import java.math.BigDecimal;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(readOnly = true)
public class UserServiceImpl implements UserService {
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final SimpMessagingTemplate template;
    private final UserUtil userUtil;

    private static final String WEB_SOCKET_PATH_TEMPLATE_BALANCE = "/auction/balance/%s";

    @Autowired
    public UserServiceImpl(UserRepository userRepository,
                           ModelMapper modelMapper,
                           SimpMessagingTemplate template, UserUtil userUtil) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.template = template;
        this.userUtil = userUtil;
    }

    @Override
    public List<UserResponse> getUsers() {
        return userRepository.findAll().stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList());
    }

    @Override
    @Transactional
    public UserResponse banUser(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        user.setBanned(true);
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse unbanUser(String username) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        user.setBanned(false);
        return modelMapper.map(userRepository.save(user), UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse addMoney(String username, BigDecimal currency) {
        User user = userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username));
        user.addMoney(currency);
        userRepository.save(user);
        sendMoneyToWsByUser(user);
        return modelMapper.map(user, UserResponse.class);
    }

    @Override
    public UserResponse getUserByUsername(String username) {
        return modelMapper.map(userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username)),
                UserResponse.class);
    }

    @Override
    @Transactional
    public UserResponse updateUser(UserRequest userRequest) {
        User oldUser = userRepository.findByUsername(userRequest.getUsername())
                .orElseThrow(() -> new UsernameNotFoundException(userRequest.getUsername()));
        oldUser.copyMainParamsFrom(userRequest);
        return modelMapper.map(userRepository.save(oldUser), UserResponse.class);
    }

    @Override
    public void sendMoneyToWsByUsername(String username) {
        sendMoneyToWsByUser(userRepository
                .findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(username)));
    }

    @Override
    public void sendMoneyToWsByUser(User user) {
        sendMoneyToWsByUser(user.getUsername(), user.getMoney());
    }

    @Override
    public ContactInfoResponse getContactInfoByLotId(Long lotId) {
        userUtil.validateBeforeGettingContactInfo(lotId);
        User user = userRepository.findCreatorByLotId(lotId)
                .orElseThrow(() -> new LotNotFoundException(lotId));
        return modelMapper.map(user, ContactInfoResponse.class);
    }

    private void sendMoneyToWsByUser(String username, BigDecimal money) {
        template.convertAndSend(String.format(WEB_SOCKET_PATH_TEMPLATE_BALANCE, username), money);
    }
}
