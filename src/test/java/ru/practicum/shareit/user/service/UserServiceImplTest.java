package ru.practicum.shareit.user.service;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.stubbing.Answer;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserServiceImplTest {

    @InjectMocks
    private UserServiceImpl userService;
    @Mock
    private UserMapper userMapper;
    @Mock
    private UserRepository userRepository;
    @Captor
    private ArgumentCaptor<User> userArgumentCaptor;

    @Test
    void createUser_whenInvoked_ThenSavedUser() {
        User userToSave = getTestUser();
        UserDto userDtoToSave = getTestUserDto();
        when(userMapper.toDto(userToSave)).thenReturn(userDtoToSave);
        when(userMapper.toEntity(userDtoToSave)).thenReturn(userToSave);
        when(userRepository.save(userToSave)).thenReturn(userToSave);

        UserDto actualUserDto = userService.createUser(userDtoToSave);

        assertEquals(userDtoToSave, actualUserDto);
        verify(userRepository).save(userToSave);
    }

    @Test
    void patchUser_whenUserFound_thenUpdatedUser() {
        User foundedUser = getTestUser();
        User updatedUser = getUpdatedUser();
        UserDto updateUserDto = getUpdateUserDto();
        when(userRepository.findById(1L)).thenReturn(Optional.of(foundedUser));
        Answer<Void> answer = invocationOnMock -> {
            UserDto userDto = invocationOnMock.getArgument(0, UserDto.class);
            User user = invocationOnMock.getArgument(1, User.class);
            user.setName(userDto.getName());
            user.setEmail(userDto.getEmail());
            return null;
        };
        doAnswer(answer).when(userMapper).updateUserFromDto(updateUserDto, foundedUser);
        when(userRepository.save(foundedUser)).thenReturn(foundedUser);
        when(userMapper.toDto(foundedUser)).thenReturn(updateUserDto);

        UserDto actualUserDto = userService.patchUser(updateUserDto);

        assertEquals(updateUserDto, actualUserDto);
        verify(userRepository).save(userArgumentCaptor.capture());
        User savedUser = userArgumentCaptor.getValue();
        assertEquals(updatedUser, savedUser);
    }

    @Test
    void patchUser_whenUserNotFound_thenNotFoundExceptionThrown() {
        UserDto userDto = getTestUserDto();
        when(userRepository.findById(userDto.getId())).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.patchUser(userDto));
    }

    @Test
    void getUserById_whenUserFound_ThenReturnedUser() {
        Long userId = 1L;
        User expectedUser = getTestUser();
        UserDto expectedUserDto = getTestUserDto();
        when(userRepository.findById(userId)).thenReturn(Optional.of(expectedUser));
        when(userMapper.toDto(expectedUser)).thenReturn(expectedUserDto);

        UserDto actualUserDto = userService.getUserById(userId);

        assertEquals(expectedUserDto, actualUserDto);
    }

    @Test
    void getUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.getUserById(userId));
    }

    @Test
    void getAllUsers_whenInvoked_ThenReturnListOfUsers() {
        List<User> expectedUsers = List.of(getTestUser());
        List<UserDto> expectedUserDtos = List.of(getTestUserDto());
        when(userRepository.findAll()).thenReturn(expectedUsers);
        when(userMapper.toUserDtoList(expectedUsers)).thenReturn(expectedUserDtos);

        List<UserDto> actualUserDtos = userService.getAllUsers();

        assertEquals(expectedUserDtos, actualUserDtos);
    }

    @Test
    void deleteUserById_whenUserFound_ThenDeleteByIdInvoked() {
        Long userId = 1L;
        User user = getTestUser();
        when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        userService.deleteUserById(userId);

        verify(userRepository).deleteById(userId);
    }

    @Test
    void deleteUserById_whenUserNotFound_thenNotFoundExceptionThrown() {
        Long userId = 0L;
        when(userRepository.findById(userId)).thenReturn(Optional.empty());

        assertThrows(NotFoundException.class,
                () -> userService.deleteUserById(userId));
    }

    User getTestUser() {
        return User.builder()
                .id(1L)
                .name("TestName")
                .email("TestEmail")
                .build();
    }

    UserDto getTestUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("TestName")
                .email("TestEmail")
                .build();
    }

    User getUpdatedUser() {
        return User.builder()
                .id(1L)
                .name("UpdatedName")
                .email("UpdatedEmail")
                .build();
    }

    UserDto getUpdateUserDto() {
        return UserDto.builder()
                .id(1L)
                .name("UpdatedName")
                .email("UpdatedEmail")
                .build();
    }
}
