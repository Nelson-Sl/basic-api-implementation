package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.api.RsController;
import com.thoughtworks.rslist.domain.HotEvents;
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

    private MockMvc mockMvc;

    @BeforeEach
    public void init() {
        mockMvc = MockMvcBuilders.standaloneSetup(new RsController()).build();
    }

    @Test
    void getAllEvents() throws Exception {
        mockMvc.perform(get("/rs/list"))
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
        mockMvc.perform(get("/rs/list/1"))
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.user.userName",is("Tony")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list/2"))
                .andExpect(jsonPath("$.eventName",is("第二条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.user.userName",is("Mark")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list/3"))
                .andExpect(jsonPath("$.eventName",is("第三条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.user.userName",is("Jenny")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void getSpecialRangeOfEvent() throws Exception {
        mockMvc.perform(get("/rs/list?start=1&end=2"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0].user.userName",is("Tony")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].user.userName",is("Mark")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list?start=2&end=3"))
                .andExpect(jsonPath("$[0].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0].user.userName",is("Mark")))
                .andExpect(jsonPath("$[1].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].user.userName",is("Jenny")))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list?start=1&end=3"))
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
    void addHotEvent() throws Exception {
        String newEvent = "{\"eventName\":\"第四条事件\",\"keyWord\":\"无分类\"}";
        mockMvc.perform(post("/rs/addEvent")
                .contentType(MediaType.APPLICATION_JSON).content(newEvent))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第一条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                .andExpect(jsonPath("$[3].eventName",is("第四条事件")))
                .andExpect(jsonPath("$[3].keyWord",is("无分类")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void changeHotEvent() throws Exception {
        mockMvc.perform(post("/rs/alterEvent")
                .param("indexStr", "1")
                .param("eventName", "特朗普辞职")
                .param("keyWord", "社会新闻"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(post("/rs/alterEvent")
                .param("indexStr", "2")
                .param("eventName", "乘风破浪的姐姐"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(post("/rs/alterEvent")
                .param("indexStr", "3")
                .param("keyWord", "娱乐新闻"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("特朗普辞职")))
                .andExpect(jsonPath("$[0].keyWord",is("社会新闻")))
                .andExpect(jsonPath("$[1].eventName",is("乘风破浪的姐姐")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[2].keyWord",is("娱乐新闻")))
                .andExpect(status().isOk())
                .andReturn();

    }

    @Test
    void deleteHotEvent() throws Exception {
        mockMvc.perform(post("/rs/deleteEvent")
                        .param("indexStr", "1")
                        .contentType("application/json; charset=UTF-8"))
                        .andExpect(status().isOk())
                        .andReturn();
        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("第二条事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].eventName",is("第三条事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(status().isOk())
                .andReturn();
    }

}
