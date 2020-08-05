package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.api.RsController;
import com.thoughtworks.rslist.api.UserController;
import com.thoughtworks.rslist.domain.HotEvents;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

//@AutoConfigureMockMvc
@SpringBootTest
public class ResponseTest {

    //@Autowired
    private MockMvc mockMvcRsController;
    private MockMvc mockMvcUserController;

    @BeforeEach
    public void init() {
        mockMvcRsController = MockMvcBuilders.standaloneSetup(new RsController()).build();
        mockMvcUserController = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }

    @Test
    void successGetSpecialEventsResponse() throws Exception {
        mockMvcRsController.perform(get("/rs/list/1"))
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
               // .andExpect(jsonPath("$.user.userName",is("Tony")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void successGetRangeEventsResponse() throws Exception {
        mockMvcRsController.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                // .andExpect(jsonPath("$[0].user.userName",is("Tony")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                // .andExpect(jsonPath("$[1].user.userName",is("Mark")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                // .andExpect(jsonPath("$[2].user.userName",is("Jenny")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void successCreatedEventsAndUserSend201WithIndex() throws Exception {
        User eventUser = new User("Amy",18,"Female", "amy@sina.cn","17458957457");
        HotEvents newEvent = new HotEvents("第四条事件","无分类", eventUser);
        ObjectMapper objectMapper = new ObjectMapper();
        String newEventStr = objectMapper.writeValueAsString(newEvent);
        String userStr = objectMapper.writeValueAsString(eventUser);
        mockMvcUserController.perform(post("/user").content(userStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));
        mockMvcRsController.perform(post("/rs/addEvent").content(newEventStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));
    }

    @Test
    void successAlterEventsSendOk() throws Exception {
        mockMvcRsController.perform(post("/rs/alterEvent")
                .param("indexStr", "1")
                .param("eventName", "特朗普辞职")
                .param("keyWord", "社会新闻"))
                .andExpect(status().isOk());
    }

    @Test
    void successDeleteEventsSendOk() throws Exception {
        mockMvcRsController.perform(post("/rs/deleteEvent")
                .param("indexStr", "1")
                .contentType("application/json; charset=UTF-8"))
                .andExpect(status().isOk());
    }

    @Test
    void getAllEventsWithoutUsersData() throws Exception {
        mockMvcRsController.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0]",not(hasKey("user"))))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1]",not(hasKey("user"))))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2]",not(hasKey("user"))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getSingleEventsWithoutUsersData() throws Exception {
        mockMvcRsController.perform(get("/rs/list/1"))
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$",not(hasKey("user"))))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getAllUsersByAddingJsonPropertyAnnotation() throws Exception {
        mockMvcUserController.perform(get("/user"))
                .andExpect(jsonPath("$[0].userName",is("Tony")))
                .andExpect(jsonPath("$[0].gender",is("Male")))
                .andExpect(jsonPath("$[1].userName",is("Mark")))
                .andExpect(jsonPath("$[1].gender",is("Male")))
                .andExpect(jsonPath("$[2].userName",is("Jenny")))
                .andExpect(jsonPath("$[2].gender",is("Female")))
                .andExpect(status().isOk());
    }
}
