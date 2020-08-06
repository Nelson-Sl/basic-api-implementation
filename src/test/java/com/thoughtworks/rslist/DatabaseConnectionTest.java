package com.thoughtworks.rslist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Repository.EventRepository;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.domain.HotEvents;
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
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DatabaseConnectionTest {
    ObjectMapper objectMapper = new ObjectMapper();
    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        eventRepository.deleteAll();
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
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        List<UserEntity> users = userRepository.findAll();
        mockMvc.perform(get("/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Mark")))
                .andExpect(jsonPath("$.gender",is("Male")))
                .andExpect(jsonPath("$.age",is(24)))
                .andExpect(jsonPath("$.email",is("mark@gmail.com")))
                .andExpect(jsonPath("$.phone",is("18888888888")));
    }

    @Test
    void shouldDeleteUserFromRepository() throws Exception {
        User user1 = new User("Mark",24,"Male","mark@gmail.com","18888888888");
        String userInfo1 = objectMapper.writeValueAsString(user1);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(delete("/deleteUser/" + userId)) 
                .andExpect(status().isOk());
        List<UserEntity> userList = userRepository.findAll();
        assertEquals(0,userList.size());
    }

    @Test
    void shouldAddingEventWhenUsersHadRegistered() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888");
        String userInfo = objectMapper.writeValueAsString(user);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        HotEvents event = new HotEvents("一个热搜事件","无分类",userId);
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/addEvent")
                        .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals("一个热搜事件",eventRepository.findAll().get(0).getEventName());
    }

    @Test
    void shouldNotAddingEventWhenUsersHadNotRegistered() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888");
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        HotEvents event = new HotEvents("一个热搜事件","无分类","33");
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUserWithHisEvents() throws Exception {
        User user1 = new User("Mark",24,"Male","mark@gmail.com","18888888888");
        String userInfo1 = objectMapper.writeValueAsString(user1);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        HotEvents event = new HotEvents("一个热搜事件","无分类",userId);
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/deleteUser/" + userId))
                .andExpect(status().isOk());
        assertEquals(0,userRepository.findAll().size());
        assertEquals(0,eventRepository.findAll().size());
    }

}
