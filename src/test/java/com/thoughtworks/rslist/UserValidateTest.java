package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.api.UserController;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.Assertions;
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
public class UserValidateTest {

    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        UserController.getUserList().clear();
    }

    @Test
    void shouldSuccessfullyAddUser() throws Exception {
        User user = new User("Alibaba",20,"Male","a@b.com","11234567890");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isOk());
        Assertions.assertEquals(1, UserController.getUserList().size());
    }

    @Test
    void userShouldNotBeAddedWithNameMoreThan8Characters() throws Exception {
        User user = new User("Alibabaal",20,"Male","a@b.com","11234567890");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userShouldNotBeAddedWithNullName() throws Exception {
        User user = new User(null,20,"Male","a@b.com","11234567890");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userShouldNotBeAddedWithNullGender() throws Exception {
        User user = new User("Alibaba",20,null,"a@b.com","11234567890");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userShouldNotBeAddedWithAgeLessThan18() throws Exception {
        User user = new User("Alibaba",17,"Male","a@b.com","11234567890");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userShouldNotBeAddedWithAgeMoreThan100() throws Exception {
        User user = new User("Alibaba",150,"Male","a@b.com","11234567890");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userShouldNotBeAddedWithEmailInIncorrectFormat() throws Exception {
        User user = new User("Alibaba",20,"Male","ab.com","11234567890");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void userShouldNotBeAddedWithPhoneInIncorrectFormat() throws Exception {
        User user = new User("Alibaba",20,"Male","a@b.com","1123456789");
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest());
    }
}
