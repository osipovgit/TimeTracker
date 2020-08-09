package ru.eltex.controller;

import org.apache.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import ru.eltex.entity.Task;
import ru.eltex.entity.TimeTrack;
import ru.eltex.entity.User;
import ru.eltex.repos.TaskRepo;
import ru.eltex.repos.TimeTrackRepo;
import ru.eltex.repos.UserRepo;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;
import java.util.List;

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
        List<Task> tasks = taskRepo.findAllByUserIdOrderById(user.getId());
        for (Task task : tasks) {
            trackRepo.deleteAllByTaskId(task.getId());
        }
        userRepo.deleteByUsername(user.getUsername());
    }

    @GetMapping("/clear_track")
    public void clearTrack(@PathVariable("username") String username, HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        List<Task> tasks = taskRepo.findAllByUserIdOrderById(user.getId());
        for (Task task : tasks) {
            trackRepo.deleteAllByTaskId(task.getId());
        }
    }

    @GetMapping("/change_status/{title}/{status}")
    public void changeStatus(@PathVariable("username") String username,
                             @PathVariable("title") String title,
                             @PathVariable("status") String status,
                             HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        Task task = taskRepo.findByUserIdAndTitle(user.getId(), title);
        if (status.equals("Start")) {
            if (trackRepo.findByTaskIdAndDate(task.getId(), new Date().getTime() / 86400000) == null) {
                TimeTrack track = new TimeTrack();
                track.setDate(new Date().getTime() / 86400000);
                track.setTaskId(task.getId());
                track.setSpentTime(0L);
                track.setStartTime(new Date().getTime());
                trackRepo.save(track);
            } else {
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, new Date().getTime());
            }
        } else if (status.equals("Stop")) {
            TimeTrack track = trackRepo.findByTaskIdAndDate(task.getId(), new Date().getTime() / 86400000);
            if (track == null) {
                TimeTrack top = trackRepo.findTopByTaskIdOrderByDateDesc(task.getId());
                Long currentTime = (new Date().getTime() - top.getStartTime()) / 1000;

                trackRepo.updateSpentTime(task.getId(), top.getDate(), (86400000 - top.getStartTime()) / 1000);
                trackRepo.updateStartTime(task.getId(), top.getDate(), null);
                for (int dayCount = 1; dayCount < new Date().getTime() / 86400000 - top.getDate() - 1; dayCount++) {
                    TimeTrack time_track = new TimeTrack();
                    time_track.setDate(top.getDate() + dayCount);
                    time_track.setTaskId(task.getId());
                    time_track.setSpentTime(86400L);
                    time_track.setStartTime(null);
                    trackRepo.save(time_track);
                    currentTime -= 86400;
                }
                trackRepo.updateSpentTime(task.getId(), new Date().getTime() / 86400000, currentTime);
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, null);

            } else {
                trackRepo.updateSpentTime(task.getId(), new Date().getTime() / 86400000,
                        (track.getSpentTime() + (new Date().getTime() - track.getStartTime())) / 1000);
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, null);
            }
        }
    }

    @GetMapping("/show_all/{start}/{stop}")
    public String showAll(@PathVariable("username") String username,
                          @PathVariable("start") Long start,
                          @PathVariable("stop") Long stop,
                          HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        List<Task> taskList = taskRepo.findAllByUserIdOrderById(user.getId());
        String json = "{";
        for (Task task : taskList) {
            List<TimeTrack> tracks = trackRepo.findAllByTaskIdOrderByDate(task.getId());
            Long time = 0L;
            for (TimeTrack track : tracks) {
                if (track.getDate() >= start / 86400000 & track.getDate() <= stop / 86400000) {
                    time += track.getSpentTime();
                }
            }
            if (time != 0) {
                if (!json.equals("")) {
                    json += ",";
                }
                json += "\"" + task.getTitle() + "\":\"" + time % 3600 + ":" + time % 60 + "\"";
            }
        }
        if (json.equals("{")) {
            json += "\"В данный период не было выполнено ни одной задачи.\":0";
        }
        json += "}";
        return json;
    }

    @GetMapping("/show_one/{start}/{stop}")
    public void showOne(@PathVariable("username") String username,
                        @PathVariable("start") String start,
                        @PathVariable("stop") String stop,
                        HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);

    }

    @GetMapping("/show_total/{start}/{stop}")
    public String showTotal(@PathVariable("username") String username,
                            @PathVariable("start") Long start,
                            @PathVariable("stop") Long stop,
                            HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        List<Task> taskList = taskRepo.findAllByUserIdOrderById(user.getId());
        Long time = 0L;
        for (Task task : taskList) {
            List<TimeTrack> tracks = trackRepo.findAllByTaskIdOrderByDate(task.getId());
            for (TimeTrack track : tracks) {
                if (track.getDate() >= start / 86400000 & track.getDate() <= stop / 86400000) {
                    time += track.getSpentTime();
                }
            }
        }
        String json = "";
        if (time == 0) {
            json += "{\"В данный период не было выполнено ни одной задачи.\":0}";
        } else {
            json += "{\"В данный период Вы уделили на задачи:\":\"" + time % 3600 + ":" + time % 60 + "\"}";
        }
        return json;
    }

    @GetMapping("/add_task/{title}")
    public String addTask(@PathVariable("username") String username,
                          @PathVariable("title") String title,
                          HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        if (taskRepo.findByUserIdAndTitle(user.getId(), title) == null) {
            Task task = new Task();
            task.setTitle(title);
            task.setUserId(user.getId());
            taskRepo.save(task);
            return "Задача добавлена!";
        } else {
            return "Задача с таким названием уже существует. Попробуйте изменить название, чтобы не запутаться :)";
        }
    }

    @GetMapping("/delete_task/{title}")
    public String deleteTask(@PathVariable("username") String username,
                             @PathVariable("title") String title,
                             HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        if (taskRepo.findByUserIdAndTitle(user.getId(), title) == null) {
            return "Задачи с таким названием не существует. Попробуйте еще раз!";
        } else {
            Task task = taskRepo.findByUserIdAndTitle(user.getId(), title);
            trackRepo.deleteAllByTaskId(task.getId());
            taskRepo.deleteByUserIdAndTitle(user.getId(), title);
            return "Задача удалена!";
        }
    }
}
