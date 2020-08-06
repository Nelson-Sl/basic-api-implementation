package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultMatcher;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;

@SpringBootTest
@AutoConfigureMockMvc
public class DatabaseConnectionTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private MockMvc mockMvc;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void shouldAddOneUserToRepository() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888");
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<UserEntity> userEntities = userRepository.findAll();
        assertEquals(1,userEntities.size());
        assertEquals("Mark",userEntities.get(0).getUserName());
    }

    @Test
    void shouldGetUserInfoFromRepository() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888");
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<UserEntity> userEntities = userRepository.findAll();
        assertEquals(1,userEntities.size());
        assertEquals("Mark",userEntities.get(0).getUserName());
        mockMvc.perform(get("/user/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Mark")))
                .andExpect(jsonPath("$.gender",is("Male")))
                .andExpect(jsonPath("$.age",is(24)))
                .andExpect(jsonPath("$.email",is("mark@gmail.com")))
                .andExpect(jsonPath("$.phone",is("18888888888")));
    }
    
}
