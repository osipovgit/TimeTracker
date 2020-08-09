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
     * @param username   username
     * @param password   password
     * @param first_name first_name
     * @param last_name  last_name
     * @param request    request
     * @param model      to view page
     * @return view redirect:/signup or /{username}/home
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

    /**
     * Метод для изменения имени пользователя.
     *
     * @param username   username
     * @param first_name first name
     * @param request    request
     * @param model      to view page
     */
    @GetMapping("/{username}/update_first_name/{first_name}")
    public void updateFirstName(@PathVariable("username") String username,
                                @PathVariable("first_name") String first_name,
                                HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        userRepo.updateFirstName(user.getUsername(), first_name);
        log.info("User " + user.getUsername() + " change first name to: " + first_name);
    }

    /**
     * Метод для изменения фамилии пользователя.
     *
     * @param username  username
     * @param last_name last name
     * @param request   request
     * @param model     to view page
     */
    @GetMapping("/{username}/update_last_name/{last_name}")
    public void updateLastName(@PathVariable("username") String username,
                               @PathVariable("last_name") String last_name,
                               HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        userRepo.updateLastName(user.getUsername(), last_name);
        log.info("User " + user.getUsername() + " change last name to: " + last_name);
    }

    /**
     * Метод для изменения пароля пользователя.
     *
     * @param username username
     * @param password password
     * @param request  request
     * @param model    to view page
     */
    @GetMapping("/{username}/update_password/{password}")
    public void updatePassword(@PathVariable("username") String username,
                               @PathVariable("password") String password,
                               HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        userRepo.updatePassword(user.getUsername(), password);
        log.info("User " + user.getUsername() + " change password");
    }
}
