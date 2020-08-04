package com.thoughtworks.rslist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
class RsListApplicationTests {
    @Autowired
    private MockMvc mockMvc;

    @Test
    void getOneSpecialEvent() throws Exception {
        mockMvc.perform(get("/rs/list/1"))
                .andExpect(content().string("第一条事件"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list/2"))
                .andExpect(content().string("第二条事件"))
                .andExpect(status().isOk())
                .andReturn();
        mockMvc.perform(get("/rs/list/3"))
                .andExpect(content().string("第三条事件"))
                .andExpect(status().isOk())
                .andReturn();
    }

}
