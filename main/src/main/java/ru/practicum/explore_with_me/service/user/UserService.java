package ru.practicum.explore_with_me.service.user;

import ru.practicum.explore_with_me.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> getAllUsers(Integer from, Integer size, Long[] arrId);

    User getUserById(long id);

    void deleteUser(long id);
}
