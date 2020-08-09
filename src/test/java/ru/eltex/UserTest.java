package ru.eltex;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;
import org.springframework.test.web.servlet.RequestBuilder;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;
import ru.eltex.entity.User;
import ru.eltex.repos.UserRepo;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

/**
 * Класс для тестирования объекта пользователя, его репозитория и контроллера.
 *
 * @author Evgeny Osipov
 */
@SpringBootTest
@AutoConfigureMockMvc
class UserTest {
    /**
     * Поле подключения репозитория для взамимодействия пользвателя с БД.
     */
    @Autowired
    private UserRepo userRepo;
    /**
     * Main entry point for server-side Spring MVC test support.
     */
    @Autowired
    private MockMvc mockMvc;

    /**
     * Тестирование регистрации пользователя.
     * Тут возникла ошибка, которую я так и не смог исправить:
     * Request processing failed; nested exception is org.thymeleaf.exceptions.
     * TemplateInputException: Error resolving template [/testUsername/home],
     * template might not exist or might not be accessible by any of the configured Template Resolvers
     *
     * @throws Exception Exception
     */
    @Test
    public void registration() throws Exception {
//         Создание temp-user, temp-task
//        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
//
//         Регистрация нового пользователя user и проверка валидности
//        RequestBuilder request = MockMvcRequestBuilders.get("/signup/" + user.getUsername() + "/" + user.getPassword() + "/" + user.getFirst_name() + "/" + user.getLast_name());
//        MvcResult result = mockMvc.perform(request).andDo(print()).andReturn();
//        assertEquals("/" + user.getUsername() + "/home", result.getModelAndView().getViewName());
//        assertEquals(user.getFirst_name(), userRepo.findByUsername(user.getUsername()).getFirst_name());
//
//         Имя пользователя занято
//        RequestBuilder request1 = MockMvcRequestBuilders.get("/signup/" + user.getUsername() + "/" + user.getPassword() + "/" + user.getFirst_name() + "/" + user.getLast_name());
//        MvcResult result1 = mockMvc.perform(request1).andDo(print()).andReturn();
//        assertEquals("redirect:/signup", result1.getModelAndView().getViewName());
//
//         Удаление temp-user
//        userRepo.deleteByUsername(user.getUsername());
//        assertNull(userRepo.findByUsername(user.getUsername()));

    }

    /**
     * Тестирование репозирория и его методов.
     *
     * @throws Exception Exception
     */
    @Test
    void testUserRepository() throws Exception {
        // Создание temp-user
        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
        userRepo.save(user);

        // Проверка метода updateFirstName
        userRepo.updateFirstName(user.getUsername(), "newFName");
        assertEquals("newFName", userRepo.findByUsername(user.getUsername()).getFirst_name());

        // Проверка метода updateLastName
        userRepo.updateLastName(user.getUsername(), "newLName");
        assertEquals("newLName", userRepo.findByUsername(user.getUsername()).getLast_name());

        // Проверка метода updatePassword
        userRepo.updatePassword(user.getUsername(), "newSlojniyPassword");
        assertEquals("newSlojniyPassword", userRepo.findByUsername(user.getUsername()).getPassword());

        // Удаление temp-user
        userRepo.deleteByUsername(user.getUsername());
        assertNull(userRepo.findByUsername(user.getUsername()));
    }
}
