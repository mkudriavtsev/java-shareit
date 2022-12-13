package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;

@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Transactional
    @Override
    public UserDto createUser(UserDto userDto) {
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("User with id " + savedUser.getId() + " created");
        return userMapper.toDto(savedUser);
    }

    @Transactional
    @Override
    public UserDto patchUser(UserDto userDto) {
        User foundedUserById = userRepository.findById(userDto.getId()).orElseThrow(() -> {
            throw new NotFoundException("User with id " + userDto.getId() + " not found");
        });
        userMapper.updateUserFromDto(userDto, foundedUserById);
        User updatedUser = userRepository.save(foundedUserById);
        log.info("User with id " + updatedUser.getId() + " updated");
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User foundedUserById = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("User with id " + id + " not found");
        });
        return userMapper.toDto(foundedUserById);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserDtoList(users);
    }

    @Transactional
    @Override
    public void deleteUserById(Long id) {
        userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("User with id " + id + " not found");
        });
        userRepository.deleteById(id);
        log.info("User with id " + id + " removed");
    }
}
