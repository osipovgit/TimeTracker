package ru.eltex.controller;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.eltex.entity.*;
import ru.eltex.repos.*;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.stream.Collectors;

/**
 * Класс - REST контроллер. Основной класс, который отвечает за                 НАПИСАТЬ ЗА ЧТО.
 * Обращение к методам происходит через ajax-запросы.
 *
 * @author Evgeny Osipov
 */
@RestController
@RequestMapping("/{username}")
public class TaskController {
    /**
     * Поле объявления переменной для логгирования.
     */
    private static final Logger log = Logger.getLogger(TaskController.class.getName());
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
}
