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
import java.text.SimpleDateFormat;
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

    @GetMapping("/delete_user")
    public void deleteUser(@PathVariable("username") String username, HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        List<Task> tasks = taskRepo.findAllByUser_id(user.getId());
        for (Task task : tasks) {
            trackRepo.deleteAllByTask_id(task.getId());
        }
        userRepo.deleteByUsername(user.getUsername());
    }

    @GetMapping("/clear_track")
    public void clearTrack(@PathVariable("username") String username, HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        List<Task> tasks = taskRepo.findAllByUser_id(user.getId());
        for (Task task : tasks) {
            trackRepo.deleteAllByTask_id(task.getId());
        }
    }

    @GetMapping("/change_status/{title}/{status}")
    public void changeStatus(@PathVariable("username") String username,
                             @PathVariable("title") String title,
                             @PathVariable("status") String status,
                             HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        Task task = taskRepo.findByUser_idAndTitle(user.getId(), title);
        if (status.equals("Start")) {
            if (trackRepo.findByTask_idAndDate(task.getId(), new Date().getTime() / 86400000) == null) {
                Time_track track = new Time_track();
                track.setDate(new Date().getTime() / 86400000);
                track.setTask_id(task.getId());
                track.setCurrent_time(0L);
                track.setStart_time(new Date().getTime());
                trackRepo.save(track);
            } else {
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, new Date().getTime());
            }
        } else if (status.equals("Stop")) {
            Time_track track = trackRepo.findByTask_idAndDate(task.getId(), new Date().getTime() / 86400000);
            if (track == null) {
                Time_track top = trackRepo.findTopByTask_idOrderByDateDesc(task.getId());
                Long currentTime = new Date().getTime() - top.getStart_time();

                trackRepo.updateCurrentTime(task.getId(), top.getDate(), 86400000 - top.getStart_time());
                trackRepo.updateStartTime(task.getId(), top.getDate(), null);
                for (int dayCount = 1; dayCount < new Date().getTime() / 86400000 - top.getDate() - 1; dayCount++) {
                    Time_track time_track = new Time_track();
                    time_track.setDate(top.getDate() + dayCount);
                    time_track.setTask_id(task.getId());
                    time_track.setCurrent_time(86400000L);
                    time_track.setStart_time(null);
                    trackRepo.save(time_track);
                    currentTime -= 86400000;
                }
                trackRepo.updateCurrentTime(task.getId(), new Date().getTime() / 86400000, currentTime);
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, null);

            } else {
                trackRepo.updateCurrentTime(task.getId(), new Date().getTime() / 86400000,
                        track.getCurrent_time() + (new Date().getTime() - track.getStart_time()));
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, null);
            }
        }
    }
}
