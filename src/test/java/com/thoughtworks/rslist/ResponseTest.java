package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.HotEvents;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@AutoConfigureMockMvc
@SpringBootTest
public class ResponseTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void successGetSpecialEventsResponse() throws Exception {
        mockMvc.perform(get("/rs/list/1"))
                .andExpect(jsonPath("$.eventName",is("第一条事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.user.userName",is("Tony")))
                .andExpect(status().isOk())
                .andReturn();
    }

    @Test
    void successGetRangeEventsResponse() throws Exception {
        mockMvc.perform(get("/rs/list"))
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
    void successCreatedEventsAndUserSend201WithIndex() throws Exception {
        User eventUser = new User("Amy",18,"Female", "amy@sina.cn","17458957457");
        HotEvents newEvent = new HotEvents("第四条事件","无分类", eventUser);
        ObjectMapper objectMapper = new ObjectMapper();
        String newEventStr = objectMapper.writeValueAsString(newEvent);
        String userStr = objectMapper.writeValueAsString(eventUser);
        mockMvc.perform(post("/user").content(userStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));
        mockMvc.perform(post("/rs/addEvent").content(newEventStr)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andExpect(content().string("3"));
    }
}
