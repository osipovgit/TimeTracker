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
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.List;

/**
 * Класс - REST контроллер. Основной класс, который отвечает за создание и удаление задач,
 * начало и окончание работы с конкретной задачей, а так же за просмотр потраченного на них времени.
 * Обращение к методам происходит через ajax-запросы.
 * В данной задаче имя пользователя передается в запросе.
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

    /**
     * Метод для удаления пользователя и всей информации о нем.
     *
     * @param username the username
     * @param request  the request
     * @param model    the model
     */
    @GetMapping("/delete_user")
    public void deleteUser(@PathVariable("username") String username, HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        List<Task> tasks = taskRepo.findAllByUserIdOrderById(user.getId());
        for (Task task : tasks) {
            trackRepo.deleteAllByTaskId(task.getId());
        }
        taskRepo.deleteAllByUserId(user.getId());
        userRepo.deleteByUsername(user.getUsername());
        log.info("User " + username + " has been deleted.");
    }

    /**
     * Метод для очистки всех задач пользователя, а так же их временных промежутков.
     *
     * @param username the username
     * @param request  the request
     * @param model    the model
     */
    @GetMapping("/clear_track")
    public void clearTrack(@PathVariable("username") String username, HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        List<Task> tasks = taskRepo.findAllByUserIdOrderById(user.getId());
        for (Task task : tasks) {
            trackRepo.deleteAllByTaskId(task.getId());
        }
        taskRepo.deleteAllByUserId(user.getId());
        log.info("User " + username + " deleted all tasks.");
    }

    /**
     * Метод начинает или заканчивает отсчет времени в определенной задаче.
     * Подсчитывает затраченное время и записявает его в поле spentTime, в таблицу time_track
     *
     * @param username the username
     * @param title    title of task
     * @param status   start or stop timer
     * @param request  the request
     * @param model    the model
     */
    @GetMapping("/change_status/{title}/{status}")
    public void changeStatus(@PathVariable("username") String username,
                             @PathVariable("title") String title,
                             @PathVariable("status") String status,
                             HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        Task task = taskRepo.findByUserIdAndTitle(user.getId(), title);
        if (status.equals("start")) {
            if (trackRepo.findByTaskIdAndDate(task.getId(), new Date().getTime() / 86400000) == null) {
                TimeTrack track = new TimeTrack();
                track.setDate(new Date().getTime() / 86400000);
                track.setTaskId(task.getId());
                track.setSpentTime(0L);
                track.setStartTime(new Date().getTime());
                trackRepo.save(track);
                log.info("User " + username + " started task: \"" + title + "\"");
            } else {
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, new Date().getTime());
                log.info("User " + username + " started task: \"" + title + "\"");
            }
        } else if (status.equals("stop")) {
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
                log.info("User " + username + " stopped task: \"" + title + "\"");

            } else {
                trackRepo.updateSpentTime(task.getId(), new Date().getTime() / 86400000,
                        (track.getSpentTime() + (new Date().getTime() - track.getStartTime())) / 1000);
                trackRepo.updateStartTime(task.getId(), new Date().getTime() / 86400000, null);
                log.info("User " + username + " stopped task: \"" + title + "\"");
            }
        }
    }

    /**
     * Метод показывает все трудозатраты пользователя Y за период N..M
     * в виде связного списка Задача - Сумма затраченного времени в виде (чч:мм),
     * сортировка по времени поступления в трекер.
     * Для ответа на вопрос, на какие задачи я потратил больше времени.
     *
     * @param username username
     * @param start    start time
     * @param stop     stop time
     * @param request  request
     * @param model    the model
     * @return json {title : total_time}
     */
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
                if (!json.equals("{")) {
                    json += ",";
                }
                json += "\"" + task.getTitle() + "\":\"" + time / 3600 + ":" + (time % 3600) / 60 + "\"";
            }
        }
        if (json.equals("{")) {
            json += "\"В данный период не было выполнено ни одной задачи.\":0";
        }
        json += "}";
        log.info("User " + username + " check all tasks.");
        return json;
    }

    /**
     * Метод показывает все временные интервалы занятые работой за период N..M по конкретной задаче.
     *
     * @param username username
     * @param title    title of task
     * @param start    start time
     * @param stop     stop time
     * @param request  request
     * @param model    the model
     * @return json {date : current_time}
     */
    @GetMapping("/show_one/{title}/{start}/{stop}")
    public String showOne(@PathVariable("username") String username,
                          @PathVariable("title") String title,
                          @PathVariable("start") Long start,
                          @PathVariable("stop") Long stop,
                          HttpServletRequest request, Model model) {
        User user = userRepo.findByUsername(username);
        Task task = taskRepo.findByUserIdAndTitle(user.getId(), title);
        String json = "{";
        List<TimeTrack> trackList = trackRepo.findAllByTaskIdOrderByDate(task.getId());
        for (TimeTrack track : trackList) {
            if (track.getDate() >= start / 86400000 & track.getDate() <= stop / 86400000) {
                if (!json.equals("{")) {
                    json += ",";
                }
                json += "\"" + new Date(track.getDate() * 86400000) + "\":\"" + track.getSpentTime() / 3600L + ":" + (track.getSpentTime() % 3600L) / 60 + "\"";
            }
        }

        if (json.equals("{")) {
            json += "\"В данный период не было выполнено ни одной задачи.\":0";
        }
        json += "}";
        log.info("User " + username + " check one tasks.");
        return json;
    }

    /**
     * Метод показывает сумму трудозатрат по всем задачам пользователя Y за период N..M.
     *
     * @param username username
     * @param start    start time
     * @param stop     stop time
     * @param request  request
     * @param model    the model
     * @return json {"total_time" : total_time}
     */
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
            json += "{\"total_time\":\"" + time / 3600 + ":" + (time % 3600) / 60 + "\"}";
        }
        log.info("User " + username + " check total of tasks.");
        return json;
    }

    /**
     * Метод для добавления задачи пользователем.
     * Для удобства пользователя задача должна иметь уникальное название.
     *
     * @param username username
     * @param title    title of task
     * @param request  request
     * @param model    the model
     * @return String [done / try again]
     */
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
            log.info("User " + username + " create task: \"" + title + "\"");
            return "Задача добавлена!";
        } else {
            return "Задача с таким названием уже существует. Попробуйте изменить название, чтобы не запутаться :)";
        }
    }

    /**
     * Метод для удаления задачи. Так же удаляет данные трекинга этой задачи.
     *
     * @param username username
     * @param title    title of task
     * @param request  request
     * @param model    the model
     * @return
     */
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
            log.info("User " + username + " delete task: \"" + title + "\"");
            return "Задача удалена!";
        }
    }

    /**
     * Не обращайте внимания, это была попытка починить тест регистрации пользователя...
     *
     * @param username username
     * @param response response
     */
    @GetMapping("/home")
    public void home(@PathVariable("username") String username, HttpServletResponse response) {
    }
}
