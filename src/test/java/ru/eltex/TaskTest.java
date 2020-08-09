package ru.eltex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.eltex.entity.Task;
import ru.eltex.entity.TimeTrack;
import ru.eltex.entity.User;
import ru.eltex.repos.TaskRepo;
import ru.eltex.repos.TimeTrackRepo;
import ru.eltex.repos.UserRepo;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Класс для тестирования объекта задачи, ее репозитория и контроллераю.
 *
 * @author Evgeny Osipov
 */
@SpringBootTest
@AutoConfigureMockMvc
class TaskTest {
    /**
     * Поле подключения репозитория для взамимодействия задач с БД.
     */
    @Autowired
    private TaskRepo taskRepo;
    /**
     * Поле подключения репозитория для взамимодействия пользвателя с БД.
     */
    @Autowired
    private UserRepo userRepo;
    /**
     * Поле подключения репозитория для взамимодействия таймеров с БД.
     */
    @Autowired
    private TimeTrackRepo trackRepo;
    /**
     * Main entry point for server-side Spring MVC test support.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Тестирование репозирория и его методов.
     *
     * @throws Exception Exception
     */
    @Test
    public void testTaskRepository() {
        // Создание temp-user, temp-task
        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
        userRepo.save(user);
        user = userRepo.findByUsername(user.getUsername());
        Task task = new Task(null, user.getId(), "I'm a teapot.");
        Task task1 = new Task(null, user.getId(), "I'm a 418.");
        taskRepo.save(task);
        taskRepo.save(task1);

        // Проверка метода findByUserIdAndTitle
        assertEquals(task.getUserId(), taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()).getUserId());
        assertEquals(task1.getUserId(), taskRepo.findByUserIdAndTitle(user.getId(), task1.getTitle()).getUserId());

        // Проверка метода findAllByUserIdOrderById
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task1);
        assertEquals(tasks, taskRepo.findAllByUserIdOrderById(user.getId()));

        // Проверка метода deleteByUserIdAndTitle
        taskRepo.deleteByUserIdAndTitle(user.getId(), task.getTitle());
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()));

        // Проверка метода deleteAllByUserId
        taskRepo.deleteAllByUserId(user.getId());
        tasks.clear();
        assertEquals(tasks, taskRepo.findAllByUserIdOrderById(user.getId()));

        // Удаление temp-user
        userRepo.deleteByUsername(user.getUsername());
        assertNull(userRepo.findByUsername(user.getUsername()));
    }

    /**
     * Тестирование TaskController.deleteUser.
     *
     * @throws Exception Exception
     */
    @Test
    public void deleteUserTest() throws Exception {
        // Создание temp-user, temp-task, temp-timeTrack
        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
        userRepo.save(user);
        Task task = new Task(null, user.getId(), "I'm a teapot.");
        Task task1 = new Task(null, user.getId(), "I'm a 418.");
        taskRepo.save(task);
        taskRepo.save(task1);
        TimeTrack timeTrack = new TimeTrack(null, task.getId(), new Date().getTime() / 86400000 - 1, 3600L, null);
        TimeTrack timeTrack1 = new TimeTrack(null, task.getId(), new Date().getTime() / 86400000, 3636L, null);
        TimeTrack timeTrack2 = new TimeTrack(null, task1.getId(), new Date().getTime() / 86400000 - 5, 3655L, null);
        TimeTrack timeTrack3 = new TimeTrack(null, task1.getId(), new Date().getTime() / 86400000 - 3, 3633L, null);
        trackRepo.save(timeTrack);
        trackRepo.save(timeTrack1);
        trackRepo.save(timeTrack2);
        trackRepo.save(timeTrack3);

        // Проверка статуса запроса и его выполнение
        this.mockMvc.perform(get("/" + user.getUsername() + "/delete_user")).andDo(print()).andExpect(status().isOk());

        // Проверка выполнения запроса [удаляется все, что связано с пользователем user]
        assertNull(userRepo.findByUsername(user.getUsername()));
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()));
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task1.getTitle()));
        assertNull(trackRepo.findByTaskIdAndDate(task.getId(), timeTrack.getDate()));
        assertNull(trackRepo.findByTaskIdAndDate(task.getId(), timeTrack1.getDate()));
        assertNull(trackRepo.findByTaskIdAndDate(task1.getId(), timeTrack2.getDate()));
        assertNull(trackRepo.findByTaskIdAndDate(task1.getId(), timeTrack3.getDate()));
    }

    /**
     * Тестирование TaskController.clearTrack.
     *
     * @throws Exception Exception
     */
    @Test
    public void clearTrackTest() throws Exception {
        // Создание temp-user, temp-task, temp-timeTrack
        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
        userRepo.save(user);
        Task task = new Task(null, user.getId(), "I'm a teapot.");
        Task task1 = new Task(null, user.getId(), "I'm a 418.");
        taskRepo.save(task);
        taskRepo.save(task1);
        TimeTrack timeTrack = new TimeTrack(null, task.getId(), new Date().getTime() / 86400000 - 1, 3600L, null);
        TimeTrack timeTrack1 = new TimeTrack(null, task.getId(), new Date().getTime() / 86400000, 3636L, null);
        TimeTrack timeTrack2 = new TimeTrack(null, task1.getId(), new Date().getTime() / 86400000 - 5, 3655L, null);
        TimeTrack timeTrack3 = new TimeTrack(null, task1.getId(), new Date().getTime() / 86400000 - 3, 3633L, null);
        trackRepo.save(timeTrack);
        trackRepo.save(timeTrack1);
        trackRepo.save(timeTrack2);
        trackRepo.save(timeTrack3);

        // Проверка статуса запроса и его выполнение
        this.mockMvc.perform(get("/" + user.getUsername() + "/clear_track")).andDo(print()).andExpect(status().isOk());

        // Проверка исполнения метода [пользователь сохраняется]
        assertEquals(user.getPassword(), userRepo.findByUsername(user.getUsername()).getPassword());
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()));
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task1.getTitle()));
        assertNull(trackRepo.findByTaskIdAndDate(task.getId(), timeTrack.getDate()));
        assertNull(trackRepo.findByTaskIdAndDate(task.getId(), timeTrack1.getDate()));
        assertNull(trackRepo.findByTaskIdAndDate(task1.getId(), timeTrack2.getDate()));
        assertNull(trackRepo.findByTaskIdAndDate(task1.getId(), timeTrack3.getDate()));

        // Удаление temp-user
        userRepo.deleteByUsername(user.getUsername());
        assertNull(userRepo.findByUsername(user.getUsername()));
    }

    /**
     * Тестирование TaskController.addTask и TaskController.deleteTask.
     *
     * @throws Exception Exception
     */
    @Test
    public void addAndDeleteTaskTest() throws Exception {
        // Создание temp-user, temp-task
        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
        userRepo.save(user);
        Task task = new Task(null, user.getId(), "I'm a teapot.");
        Task task1 = new Task(null, user.getId(), "I'm a 418.");

        // Добавление задачи task | успешное
        RequestBuilder request = MockMvcRequestBuilders.get("/" + user.getUsername() + "/add_task/" + task.getTitle());
        MvcResult result = mockMvc.perform(request).andDo(print()).andReturn();
        assertEquals("Задача добавлена!", result.getResponse().getContentAsString());
        assertEquals(task.getUserId(), taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()).getUserId());

        // Добавление задачи task1 | успешное
        RequestBuilder request1 = MockMvcRequestBuilders.get("/" + user.getUsername() + "/add_task/" + task1.getTitle());
        result = mockMvc.perform(request1).andDo(print()).andReturn();
        assertEquals("Задача добавлена!", result.getResponse().getContentAsString());
        assertEquals(task.getUserId(), taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()).getUserId());

        // Добавление задачи task | не успешное
        MvcResult result1 = mockMvc.perform(request).andDo(print()).andReturn();
        assertEquals("Задача с таким названием уже существует. Попробуйте изменить название, чтобы не запутаться :)", result1.getResponse().getContentAsString());
        assertEquals(task.getUserId(), taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()).getUserId());

        // Добавление задачи task1 | не успешное
        result1 = mockMvc.perform(request1).andDo(print()).andReturn();
        assertEquals("Задача с таким названием уже существует. Попробуйте изменить название, чтобы не запутаться :)", result1.getResponse().getContentAsString());
        assertEquals(task.getUserId(), taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()).getUserId());

        // Удаление задачи task | успешное
        RequestBuilder request2 = MockMvcRequestBuilders.get("/" + user.getUsername() + "/delete_task/" + task.getTitle());
        MvcResult result2 = mockMvc.perform(request2).andDo(print()).andReturn();
        assertEquals("Задача удалена!", result2.getResponse().getContentAsString());
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()));

        // Удаление задачи task1 | успешное
        RequestBuilder request3 = MockMvcRequestBuilders.get("/" + user.getUsername() + "/delete_task/" + task1.getTitle());
        MvcResult result3 = mockMvc.perform(request3).andDo(print()).andReturn();
        assertEquals("Задача удалена!", result3.getResponse().getContentAsString());
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task1.getTitle()));

        // Удаление задачи task | не успешное
        result2 = mockMvc.perform(request2).andDo(print()).andReturn();
        assertEquals("Задачи с таким названием не существует. Попробуйте еще раз!", result2.getResponse().getContentAsString());
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()));

        // Удаление задачи task1 | не успешное
        result3 = mockMvc.perform(request3).andDo(print()).andReturn();
        assertEquals("Задачи с таким названием не существует. Попробуйте еще раз!", result3.getResponse().getContentAsString());
        assertNull(taskRepo.findByUserIdAndTitle(user.getId(), task1.getTitle()));

        // Удаление temp-user
        userRepo.deleteByUsername(user.getUsername());
        assertNull(userRepo.findByUsername(user.getUsername()));
    }

    /**
     * Тестирование TaskController.showOne.
     *
     * @throws Exception Exception
     */
    @Test
    public void showOneTest() throws Exception {

    }

    /**
     * Тестирование TaskController.showAll.
     *
     * @throws Exception Exception
     */
    @Test
    public void showAllTest() throws Exception {

    }

    /**
     * Тестирование TaskController.showTotal.
     *
     * @throws Exception Exception
     */
    @Test
    public void showTotalTest() throws Exception {

    }
}
