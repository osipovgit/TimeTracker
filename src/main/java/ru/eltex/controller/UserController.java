package ru.eltex.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import ru.eltex.entity.User;
import ru.eltex.repos.TaskRepo;
import ru.eltex.repos.TimeTrackRepo;
import ru.eltex.repos.UserRepo;

import javax.servlet.http.HttpServletRequest;

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
    @GetMapping("/signup/{username}/{password}/{first_name}/{last_name}")
    public String signUpNewUser(@PathVariable("username") String username,
                                @PathVariable("password") String password,
                                @PathVariable("first_name") String first_name,
                                @PathVariable("last_name") String last_name,
                                HttpServletRequest request, Model model) {
        User user = new User(null, username, password, first_name, last_name);
        User userFromDb = userRepo.findByUsername(user.getUsername());
        if (userFromDb != null | username == null | password == null | first_name == null | last_name == null) {
            return "redirect:/signup";
        }
        log.info("User " + user.getUsername() + " is registered.");
        userRepo.save(user);
        return "/" + user.getUsername() + "/home";
    }
}
