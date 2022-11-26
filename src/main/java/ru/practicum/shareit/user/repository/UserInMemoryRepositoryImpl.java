package ru.practicum.shareit.user.repository;

import org.springframework.stereotype.Repository;
import ru.practicum.shareit.user.model.User;

import java.util.*;

@Repository
public class UserInMemoryRepositoryImpl implements UserRepository {

    private final Map<Long, User> userMap = new HashMap<>();
    private long currentID;

    private long getNextId() {
        return ++currentID;
    }

    @Override
    public List<User> findAll() {
        return new ArrayList<>(userMap.values());
    }

    @Override
    public Optional<User> findById(Long id) {
        return userMap.containsKey(id) ? Optional.of(userMap.get(id)) : Optional.empty();
    }

    @Override
    public Optional<User> findByEmail(String email) {
        return userMap.values()
                .stream()
                .filter(user -> user.getEmail().equals(email))
                .findAny();
    }

    @Override
    public User save(User user) {
        user.setId(getNextId());
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public User update(User user) {
        userMap.put(user.getId(), user);
        return user;
    }

    @Override
    public void delete(Long id) {
        userMap.remove(id);
    }
}
