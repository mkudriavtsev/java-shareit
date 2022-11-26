package ru.practicum.shareit.user.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import ru.practicum.shareit.exception.EmailAlreadyExistException;
import ru.practicum.shareit.exception.NotFoundException;
import ru.practicum.shareit.user.dto.UserDto;
import ru.practicum.shareit.user.mapper.UserMapper;
import ru.practicum.shareit.user.model.User;
import ru.practicum.shareit.user.repository.UserRepository;

import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {

    private final UserMapper userMapper;
    private final UserRepository userRepository;

    @Override
    public UserDto createUser(UserDto userDto) {
        userRepository
                .findByEmail(userDto.getEmail())
                .ifPresent((u) -> {
                    throw new EmailAlreadyExistException("Пользователь с email " + userDto.getEmail() + " уже зарегистрирован");
                });
        User user = userMapper.toEntity(userDto);
        User savedUser = userRepository.save(user);
        log.info("Пользователь с id " + savedUser.getId() + " создан");
        return userMapper.toDto(savedUser);
    }

    @Override
    public UserDto patchUser(UserDto userDto) {
        User foundedUserById = userRepository.findById(userDto.getId()).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с id " + userDto.getId() + " не найден");
        });
        if (Objects.nonNull(userDto.getEmail())) {
            Optional<User> foundedUserByEmail = userRepository.findByEmail(userDto.getEmail());
            if (foundedUserByEmail.isPresent()) {
                if (!foundedUserByEmail.get().getId().equals(userDto.getId())) {
                    throw new EmailAlreadyExistException("Пользователь с email " + userDto.getEmail() + " уже зарегистрирован");
                }
            }
            foundedUserById.setEmail(userDto.getEmail());
        }
        if (Objects.nonNull(userDto.getName())) {
            foundedUserById.setName(userDto.getName());
        }
        User updatedUser = userRepository.update(foundedUserById);
        log.info("Пользователь с id " + updatedUser.getId() + " изменен");
        return userMapper.toDto(updatedUser);
    }

    @Override
    public UserDto getUserById(Long id) {
        User foundedUserById = userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        });
        return userMapper.toDto(foundedUserById);
    }

    @Override
    public List<UserDto> getAllUsers() {
        List<User> users = userRepository.findAll();
        return userMapper.toUserDtoList(users);
    }

    @Override
    public void deleteUserById(Long id) {
        userRepository.findById(id).orElseThrow(() -> {
            throw new NotFoundException("Пользователь с id " + id + " не найден");
        });
        userRepository.delete(id);
        log.info("Пользователь с id " + id + " удален");
    }
}
