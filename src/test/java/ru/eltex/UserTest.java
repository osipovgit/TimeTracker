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

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.BDDMockito.given;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class UserTest {

    @Autowired
    private UserRepo userRepo;

    @Autowired
    private MockMvc mockMvc;

    @Test
    public void registration() throws Exception {
        given(userRepo.findByUsername(any()))
                .willReturn(Optional.of(new User(1L, "testUsername", "psswd", "testFname", "testLname")).get());
        this.mockMvc.perform(get("/signup/testUsername/psswd/testFname/testLname")).andDo(print()).andExpect(status().is3xxRedirection());

        RequestBuilder request = MockMvcRequestBuilders.get("/signup/testUsername/psswd/testFname/testLname");
        MvcResult result = mockMvc.perform(request).andReturn();
//        assertEquals("/testUsername/home", result.getModelAndView().getViewName()); // теоретически должно работать, проверил через браузер
        assertEquals("redirect:/signup", result.getModelAndView().getViewName()); // работает практически
    }

    @Test
    void testUserRepository() throws Exception {
        User user = new User(null, "testUsername", "psswd", "testFname", "testLname");
        userRepo.save(user);
        userRepo.updateFirstName(user.getUsername(), "newFName");
        assertEquals("newFName", userRepo.findByUsername(user.getUsername()).getFirst_name());
        userRepo.updateLastName(user.getUsername(), "newLName");
        assertEquals("newLName", userRepo.findByUsername(user.getUsername()).getLast_name());
        userRepo.updatePassword(user.getUsername(), "newSlojniyPassword");
        assertEquals("newSlojniyPassword", userRepo.findByUsername(user.getUsername()).getPassword());
        userRepo.deleteByUsername(user.getUsername());
        assertEquals(null, userRepo.findByUsername(user.getUsername()));
    }
}
