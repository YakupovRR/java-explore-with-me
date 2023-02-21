package ru.practicum.exploreWithMe.service.user;

import ru.practicum.exploreWithMe.model.User;

import java.util.List;

public interface UserService {
    User addUser(User user);

    List<User> getAllUsers(Integer from, Integer size, Long[] arrId);

    User getUserById(long id);

    void deleteUser(long id);
}
