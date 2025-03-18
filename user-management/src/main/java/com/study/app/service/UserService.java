package com.study.app.service;

import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import com.study.app.model.dto.UserDto;
import com.study.app.model.entity.UserEntity;
import com.study.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.transaction.support.TransactionTemplate;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;

    private final AuthenticationService authenticationService;

    private final TransactionTemplate transactionTemplate;

    public void create(String username, String authenticationId, String password) {
        if (userRepository.existsByUsername(username)) {
            throw new ExpandableException(ExceptionCode.DUPLICATED_USERNAME);
        }

        UserEntity userEntity = UserEntity.of(username);

        userEntity = createUserAndCommit(userEntity);
        try {
            authenticationService.createAuthentication(userEntity.getId(), authenticationId, password);
        } catch (ExpandableException e) {
            userRepository.delete(userEntity);
            throw e;
        }
    }

    private UserEntity createUserAndCommit(UserEntity userEntity) {
        return transactionTemplate.execute(status -> {
            try {
                return userRepository.saveAndFlush(userEntity);
            } catch (RuntimeException e) {
                status.setRollbackOnly();
                throw new ExpandableException(ExceptionCode.INTERNAL_SERVER_ERROR, e);
            }
        });
    }

    @Transactional
    public void update(Long requesterId, Long userId, String username) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_USER));

        if (!userEntity.getId().equals(requesterId)) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_USER);
        }

        userEntity.update(username);

        userRepository.save(userEntity);
    }

    @Transactional
    public void withdraw(Long requesterId, Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_USER));

        if (!userEntity.getId().equals(requesterId)) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_USER);
        }

        userRepository.withdraw(userEntity.getId());
    }

    @Transactional(readOnly = true)
    public UserDto detail(Long requesterId, Long userId) {
        UserEntity userEntity = userRepository.findById(userId)
                .orElseThrow(() -> new ExpandableException(ExceptionCode.NOT_FOUND_USER));

        if (!userEntity.getId().equals(requesterId)) {
            throw new ExpandableException(ExceptionCode.INVALID_ACCESS_USER);
        }

        return UserDto.from(userEntity);
    }
}
