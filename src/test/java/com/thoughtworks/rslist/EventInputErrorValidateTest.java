package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.Repository.EventRepository;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.domain.HotEvents;
import com.thoughtworks.rslist.domain.User;
import org.junit.jupiter.api.BeforeEach;
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
public class EventInputErrorValidateTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserRepository userRepository;

    @BeforeEach
    public void setUp() {
        eventRepository.deleteAll();
        userRepository.deleteAll();
    }

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
        User user = User.builder().userName("Alibabaal").age(20).gender("Male")
                .email("a@b.com").phone("11234567890").vote(10).build();
        HotEvents event = HotEvents.builder()
                .eventName("特朗普辞职").keyWord("无分类").user(user).userId(1).voteNum(10).build();
        ObjectMapper objectMapper = new ObjectMapper();
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/event")
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
