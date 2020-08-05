package com.thoughtworks.rslist;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
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
                .andExpect(status().isBadRequest());

    }

    @Test
    void get400BadRequestIfEventIndexIsOutOfBound() throws Exception {
        mockMvc.perform(get("/rs/list/10"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.error",is("invalid index")));
    }
}
