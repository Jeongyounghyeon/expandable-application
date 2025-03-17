package com.study.app.service;

import com.study.app.exception.ExceptionCode;
import com.study.app.exception.ExpandableException;
import com.study.app.model.dto.UserDto;
import com.study.app.model.entity.UserEntity;
import com.study.app.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestConstructor;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.BDDMockito.given;
import static org.mockito.BDDMockito.willThrow;
import static org.mockito.Mockito.*;

@SpringBootTest(classes = UserService.class)
@RequiredArgsConstructor
@TestConstructor(autowireMode = TestConstructor.AutowireMode.ALL)
class UserServiceTest {

    private final UserService userService;

    @MockitoBean
    private final UserRepository userRepository;

    @MockitoBean
    private final AuthenticationService authenticationService;

    @MockitoBean
    private final TransactionTemplate transactionTemplate;

    @Test
    public void create() {
        String username = "username";
        String authenticationId = "authenticationId";
        String password = "password";

        UserEntity userEntity = mock(UserEntity.class);
        given(userEntity.getId()).willReturn(anyLong());

        given(userRepository.existsByUsername(username)).willReturn(false);
        given(userRepository.saveAndFlush(any())).willReturn(mock(UserEntity.class));
        given(transactionTemplate.execute(any())).willAnswer(invocation -> userRepository.saveAndFlush(userEntity));

        assertDoesNotThrow(() -> userService.create(username, authenticationId, password));
    }

    @Test
    public void create_실패_테스트_인증_생성_실패() {
        String username = "username";
        String authenticationId = "authenticationId";
        String password = "password";

        UserEntity userEntity = mock(UserEntity.class);
        given(userEntity.getId()).willReturn(anyLong());

        given(userRepository.existsByUsername(username)).willReturn(false);
        given(userRepository.saveAndFlush(any())).willReturn(mock(UserEntity.class));
        given(transactionTemplate.execute(any())).willAnswer(invocation -> userRepository.saveAndFlush(userEntity));
        doThrow(new ExpandableException(ExceptionCode.AUTHENTICATION_CREATE_FAILED))
                .when(authenticationService).createAuthentication(anyLong(), any(), any());

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.create(username, authenticationId, password));
        assertEquals(e.getCode(), ExceptionCode.AUTHENTICATION_CREATE_FAILED);
    }

    @Test
    public void create_실패_테스트_중복된_username() {
        String username = "username";
        String authenticationId = "authenticationId";
        String password = "password";

        given(userRepository.existsByUsername(username)).willReturn(true);

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.create(username, authenticationId, password));
        assertEquals(e.getCode(), ExceptionCode.DUPLICATED_USERNAME);
    }

    @Test
    public void update() {
        Long requesterId = 1L;
        Long userId = 1L;

        UserEntity userEntity = mock(UserEntity.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userEntity.getId()).willReturn(userId);

        String updatedUsername = "updated_username";
        userService.update(requesterId, userId, updatedUsername);

        verify(userRepository).save(any());
    }

    @Test
    public void update_실패_테스트_없는_유저() {
        Long requesterId = 1L;
        Long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        String updatedUsername = "updated_username";

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.update(requesterId, userId, updatedUsername));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_USER);
    }

    @Test
    public void update_실패_테스트_본인이_아닌_유저의_요청() {
        Long requesterId = 2L;
        Long userId = 1L;

        UserEntity userEntity = mock(UserEntity.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userEntity.getId()).willReturn(userId);

        String updatedUsername = "updated_username";

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.update(requesterId, userId, updatedUsername));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_USER);
    }

    @Test
    public void delete() {
        Long requesterId = 1L;
        Long userId = 1L;

        UserEntity userEntity = mock(UserEntity.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userEntity.getId()).willReturn(userId);

        userService.withdraw(requesterId, userId);

        verify(userRepository).withdraw(any());
    }

    @Test
    public void delete_실패_테스트_없는_유저() {
        Long requesterId = 1L;
        Long userId = 1L;

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.withdraw(requesterId, userId));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_USER);
    }

    @Test
    public void delete_실패_본인이_아닌_유저의_요청() {
        Long requesterId = 2L;
        Long userId = 1L;

        UserEntity userEntity = mock(UserEntity.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userEntity.getId()).willReturn(userId);

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.withdraw(requesterId, userId));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_USER);
    }

    @Test
    public void detail() {
        Long requesterId = 1L;
        Long userId = 1L;
        String username = "username";

        UserEntity userEntity = mock(UserEntity.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userEntity.getId()).willReturn(userId);
        given(userEntity.getUsername()).willReturn(username);

        UserDto userDto = userService.detail(requesterId, userId);

        assertEquals(userDto.getUsername(), username);
    }

    @Test
    public void detail_실패_테스트_없는_유저() {
        Long requesterId = 1L;
        Long userId = 1L;
        String username = "username";

        given(userRepository.findById(userId)).willReturn(Optional.empty());

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.detail(requesterId, userId));
        assertEquals(e.getCode(), ExceptionCode.NOT_FOUND_USER);
    }

    @Test
    public void detail_실패_테스트_본인이_아닌_유저의_요청() {
        Long requesterId = 2L;
        Long userId = 1L;
        String username = "username";

        UserEntity userEntity = mock(UserEntity.class);

        given(userRepository.findById(userId)).willReturn(Optional.of(userEntity));
        given(userEntity.getId()).willReturn(userId);
        given(userEntity.getUsername()).willReturn(username);

        ExpandableException e = assertThrows(ExpandableException.class, () -> userService.detail(requesterId, userId));
        assertEquals(e.getCode(), ExceptionCode.INVALID_ACCESS_USER);
    }
}
