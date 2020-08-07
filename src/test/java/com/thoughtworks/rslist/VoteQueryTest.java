package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.Repository.EventRepository;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.Repository.VoteRepository;
import com.thoughtworks.rslist.domain.HotEvents;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.time.Month;
import java.time.format.DateTimeFormatter;

import static org.hamcrest.Matchers.hasKey;
import static org.hamcrest.Matchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class VoteQueryTest {
    ObjectMapper objectMapper = new ObjectMapper();

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private EventRepository eventRepository;
    @Autowired
    private VoteRepository voteRepository;

    @BeforeEach
    public void setUp() {
        userRepository.deleteAll();
        eventRepository.deleteAll();
        voteRepository.deleteAll();
    }

    @Test
    void shouldQueryVoteRecordBetweenTime() throws Exception {
        User user1 = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo1 = objectMapper.writeValueAsString(user1);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        HotEvents event = new HotEvents("一个热搜事件","无分类",userId,10);
        String eventInfo = objectMapper.writeValueAsString(event);
        String eventId = mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        LocalDateTime voteTime =
                LocalDateTime.of(2019, Month.MARCH,21,12,5,12);
        Vote userVote = new Vote(5, voteTime,userId);

        String userVoteInfo = objectMapper.writeValueAsString(userVote);

        mockMvc.perform(post("/rs/vote/"+eventId)
                .content(userVoteInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());

        mockMvc.perform(get("/rs/vote?startTime=2018-03-10 10:00:00&endTime=2020-01-01 10:00:00"))
                .andExpect(jsonPath("$[0].voteNum",is(5)))
                .andExpect(jsonPath("$[0]",hasKey("voteTime")))
                .andExpect(jsonPath("$[0].userId",is(userId)))
                .andExpect(status().isOk());
    }
}
