package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class UserInputValidateTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        // mockMvc = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }

    @Test
    void userWithNameMoreThan8CharactersCanNotRegister() throws Exception {
        User user = new User("Alibabaal",20,"Male","a@b.com","11234567890",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userWithNullNameCanNotRegister() throws Exception {
        User user = new User(null,20,"Male","a@b.com","11234567890",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userWithNullGenderCanNotRegister() throws Exception {
        User user = new User("Alibaba",20,null,"a@b.com","11234567890",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userWithAgeLessThan18CanNotRegister() throws Exception {
        User user = new User("Alibaba",17,"Male","a@b.com","11234567890",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userWithAgeMoreThan100CanNotRegister() throws Exception {
        User user = new User("Alibaba",150,"Male","a@b.com","11234567890",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userWithIncorrectEmailFormatCanNotRegister() throws Exception {
        User user = new User("Alibaba",20,"Male","ab.com","11234567890",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userWithIncorrectPhoneFormatCanNotRegister() throws Exception {
        User user = new User("Alibaba",20,"Male","a@b.com","1123456789",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }
}
