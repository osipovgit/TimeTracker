package ru.eltex.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PostMapping;
import ru.eltex.entity.User;
import ru.eltex.repos.TaskRepo;
import ru.eltex.repos.TimeTrackRepo;
import ru.eltex.repos.UserRepo;

/**
 * Класс-контроллер для управления пользователем.
 */
@Controller
public class UserController {
    /**
     * Поле объявления переменной для логгирования
     */
    private static final Logger log = Logger.getLogger(UserController.class.getName());
    /**
     * Поле подключения репозитория для взамимодействия пользвателя с БД.
     */
    @Autowired
    private UserRepo userRepo;
    /**
     * Поле подключения репозитория для взамимодействия задач с БД.
     */
    @Autowired
    private TaskRepo taskRepo;
    /**
     * Поле подключения репозитория для взамимодействия таймеров с БД.
     */
    @Autowired
    private TimeTrackRepo trackRepo;

    /**
     * Регистрация пользователя. Выполняет поиск имени пользователя в БД, проверяет на уникальность:
     * - если нет: добавляет пользователя в БД и переходит на главную страницу [{username}/home];
     * - если да:  возвращает на страницу регистрации [signup].
     *
     * @param user  it receives data from forms
     * @param model to view page
     * @return view /signup or redirect:/{username}/home
     */
    @PostMapping("/signup")
    public String signUpNewUser(User user, Model model) {
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if (userFromDb != null) {
            return "redirect:/signup";
        }
        log.info("User " + user.getUsername() + " is registered.");
        userRepo.save(user);
        return "redirect:/{" + user.getUsername() + "}/home";
    }
}
