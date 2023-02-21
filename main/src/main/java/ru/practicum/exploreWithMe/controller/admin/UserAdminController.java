package ru.practicum.exploreWithMe.controller.admin;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import ru.practicum.exploreWithMe.model.dto.UserDto;
import ru.practicum.exploreWithMe.model.mapper.UserMapper;
import ru.practicum.exploreWithMe.service.user.UserService;

import javax.validation.Valid;
import javax.validation.constraints.Positive;
import javax.validation.constraints.PositiveOrZero;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@Slf4j
@RequiredArgsConstructor
@Validated
@RequestMapping(path = "/admin/users")
public class UserAdminController {
    private final UserService userService;
    private final UserMapper userMapper;

    @PostMapping
    public ResponseEntity<UserDto> addUser(@RequestBody @Valid UserDto userDto) {
        log.info("Получен Post запрос к эндпоинту admin/users");
        return new ResponseEntity<>(userMapper.toDto(userService.addUser(userMapper.toUser(userDto))), HttpStatus.CREATED);
    }

    @GetMapping
    public List<UserDto> getUsers(@RequestParam(required = false) Long[] ids,
                                  @RequestParam(defaultValue = "10") @Positive int size,
                                  @RequestParam(defaultValue = "0") @PositiveOrZero int from) {
        log.info("Получен Get запрос к эндпоинту admin/users");

        return userService.getAllUsers(from, size, ids).stream()
                .map(userMapper::toDto)
                .collect(Collectors.toList());
    }

    @DeleteMapping("/{userId}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteUser(@PathVariable long userId) {
        log.info("Получен Delete запрос к эндпоинту admin/users", userId);
        userService.deleteUser(userId);
    }
}
