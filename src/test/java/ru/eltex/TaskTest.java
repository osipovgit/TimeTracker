package ru.eltex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import ru.eltex.entity.Task;
import ru.eltex.entity.User;
import ru.eltex.repos.TaskRepo;
import ru.eltex.repos.UserRepo;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
@AutoConfigureMockMvc
class TaskTest {
    @Autowired
    private TaskRepo taskRepo;

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void testTaskRepository() {
        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
        userRepo.save(user);
        user = userRepo.findByUsername(user.getUsername());
        Task task = new Task(null, user.getId(), "I'm a teapot.");
        Task task1 = new Task(null, user.getId(), "I'm a 418.");
        taskRepo.save(task);
        taskRepo.save(task1);
        assertEquals(task.getUserId(), taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()).getUserId());
        List<Task> tasks = new ArrayList<>();
        tasks.add(task);
        tasks.add(task1);
        assertEquals(tasks, taskRepo.findAllByUserIdOrderById(user.getId()));
        userRepo.deleteByUsername(user.getUsername());
        assertEquals(null, userRepo.findByUsername(user.getUsername()));
        taskRepo.deleteByUserIdAndTitle(user.getId(), task.getTitle());
        assertEquals(null, taskRepo.findByUserIdAndTitle(user.getId(), task.getTitle()));
        taskRepo.deleteAllByUserId(user.getId());
        tasks.clear();
        assertEquals(tasks, taskRepo.findAllByUserIdOrderById(user.getId()));
    }

    @Test
    public void() {

    }
}
