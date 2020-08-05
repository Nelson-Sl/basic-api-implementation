package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.api.RsController;
import com.thoughtworks.rslist.api.UserController;
import com.thoughtworks.rslist.domain.HotEvents;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.hamcrest.Matchers.hasKey;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static org.hamcrest.Matchers.is;

@SpringBootTest
// @AutoConfigureMockMvc
// @DirtiesContext(classMode = DirtiesContext.ClassMode.AFTER_EACH_TEST_METHOD)
class RsListApplicationTests {

    private MockMvc mockMvcRsController;
    private MockMvc mockMvcUserController;

    @BeforeEach
    public void init() {
        mockMvcRsController = MockMvcBuilders.standaloneSetup(new RsController()).build();
        mockMvcUserController = MockMvcBuilders.standaloneSetup(new UserController()).build();
    }

    @Test
    void getAllEvents() throws Exception {
        mockMvcRsController.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0]",hasKey("user")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1]",hasKey("user")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2]",hasKey("user")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getOneSpecialEvent() throws Exception {
        mockMvcRsController.perform(get("/rs/list/1"))
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.user.userName",is("Tony")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(get("/rs/list/2"))
                .andExpect(jsonPath("$.eventName",is("第二条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.user.userName",is("Mark")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(get("/rs/list/3"))
                .andExpect(jsonPath("$.eventName",is("第三条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.user.userName",is("Jenny")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getSpecialRangeOfEvent() throws Exception {
        mockMvcRsController.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0].user.userName",is("Tony")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].user.userName",is("Mark")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(get("/rs/list?start=2&end=3"))
                .andExpect(jsonPath("$[0].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0].user.userName",is("Mark")))
                .andExpect(jsonPath("$[1].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].user.userName",is("Jenny")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(get("/rs/list?start=1&end=3"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0].user.userName",is("Tony")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].user.userName",is("Mark")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2].user.userName",is("Jenny")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void addHotEventWithOldUser() throws Exception {
        User eventUser = new User("Tony",28,"Male", "tony@sina.cn","17458957454");
        HotEvents newEvent = new HotEvents("第四条事件","无分类", eventUser);
        ObjectMapper objectMapper = new ObjectMapper();
        String newEventStr = objectMapper.writeValueAsString(newEvent);
        String userStr = objectMapper.writeValueAsString(eventUser);

        mockMvcUserController.perform(post("/user").content(userStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(3, UserController.getUserList().size());

        mockMvcRsController.perform(post("/rs/addEvent")
                .contentType(MediaType.APPLICATION_JSON).content(newEventStr))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0]",hasKey("user")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1]",hasKey("user")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2]",hasKey("user")))
                .andExpect(jsonPath("$[3].eventName",is("第四条事件")))
                .andExpect(jsonPath("$[3].keyWord",is("无分类")))
                .andExpect(jsonPath("$[3]",hasKey("user")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void addHotEventWithNewUser() throws Exception {
        User eventUser = new User("Amy",18,"Female", "amy@sina.cn","17458957457");
        HotEvents newEvent = new HotEvents("第四条事件","无分类", eventUser);
        ObjectMapper objectMapper = new ObjectMapper();
        String newEventStr = objectMapper.writeValueAsString(newEvent);
        String userStr = objectMapper.writeValueAsString(eventUser);

        mockMvcUserController.perform(post("/user").content(userStr).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk());
        Assertions.assertEquals(4, UserController.getUserList().size());

        mockMvcRsController.perform(post("/rs/addEvent")
                .contentType(MediaType.APPLICATION_JSON).content(newEventStr))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0]",hasKey("user")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1]",hasKey("user")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2]",hasKey("user")))
                .andExpect(jsonPath("$[3].eventName",is("第四条事件")))
                .andExpect(jsonPath("$[3].keyWord",is("无分类")))
                .andExpect(jsonPath("$[3]",hasKey("user")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void changeHotEvent() throws Exception {
        mockMvcRsController.perform(post("/rs/alterEvent")
                .param("indexStr", "1")
                .param("eventName", "特朗普辞职")
                .param("keyWord", "社会新闻"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(post("/rs/alterEvent")
                .param("indexStr", "2")
                .param("eventName", "乘风破浪的姐姐"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(post("/rs/alterEvent")
                .param("indexStr", "3")
                .param("keyWord", "娱乐新闻"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvcRsController.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("特朗普辞职")))
                .andExpect(jsonPath("$[0].keyWord",is("社会新闻")))
                .andExpect(jsonPath("$[0]",hasKey("user")))
                .andExpect(jsonPath("$[1].eventName",is("乘风破浪的姐姐")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0]",hasKey("user")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("娱乐新闻")))
                .andExpect(jsonPath("$[0]",hasKey("user")))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void deleteHotEvent() throws Exception {
        mockMvcRsController.perform(post("/rs/deleteEvent")
                        .param("indexStr", "1")
                        .contentType("application/json; charset=UTF-8"))
                        .andExpect(status().isOk())
                        .andReturn();
        mockMvcRsController.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0]",hasKey("user")))
                .andExpect(jsonPath("$[1].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1]",hasKey("user")))
                .andExpect(status().isOk())
                .andReturn();
    }

}
