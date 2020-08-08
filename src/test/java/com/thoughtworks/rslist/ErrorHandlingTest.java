package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.HotEvents;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import static org.hamcrest.Matchers.is;

@SpringBootTest
@AutoConfigureMockMvc
public class ErrorHandlingTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void get400BadRequestIfStartIsLargerThanEndInEventRangeSearch() throws Exception {
        mockMvc.perform(get("/rs/list?start=2&end=1"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid request param")));

    }

    @Test
    void get400BadRequestIfEventIndexIsOutOfBound() throws Exception {
        mockMvc.perform(get("/rs/list/10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid index")));
    }

    @Test
    void getExceptionIfUserInputOfEventIsNotValid() throws Exception {
        User user = new User("Alibabaal",20,"Male","a@b.com","11234567890",10);
        HotEvents event = new HotEvents("特朗普辞职","无分类","user",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid param")));
    }

    @Test
    void getExceptionIfUserInputIsNotValid() throws Exception {
        User user = new User(null,20,"Male","a@b.com","11234567890",10);
        ObjectMapper objectMapper = new ObjectMapper();
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/user")
                .content(userInfo).contentType("application/json; charset=UTF-8"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid user")));
    }
}
