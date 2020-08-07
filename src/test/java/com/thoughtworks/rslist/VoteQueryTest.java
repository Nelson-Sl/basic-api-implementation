package com.thoughtworks.rslist;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.Repository.EventRepository;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.Repository.VoteRepository;
import com.thoughtworks.rslist.domain.HotEvents;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import org.junit.jupiter.api.AfterEach;
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

    private User user;
    private HotEvents event;
    private Vote vote;
    private String userId;
    private String eventId;
    private String voteId;

    @BeforeEach
    public void setUp() throws Exception {
        user = User.builder()
                .userName("Mark")
                .age(24)
                .gender("Male")
                .email("mark@gmail.com")
                .phone("18888888888")
                .vote(10).build();
        String userInfo = objectMapper.writeValueAsString(user);
        userId = mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        event = HotEvents.builder().eventName("一个热搜事件").keyWord("无分类")
                .userId(userId).voteNum(10).build();
        String eventInfo = objectMapper.writeValueAsString(event);
        eventId = mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        LocalDateTime voteTime =
                LocalDateTime.of(2019, Month.MARCH,21,12,5,12);
        vote = Vote.builder().voteNum(5).voteTime(voteTime).userId(userId).build();
        String userVoteInfo = objectMapper.writeValueAsString(vote);

        voteId = mockMvc.perform(post("/rs/vote/"+eventId)
                .content(userVoteInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

    }

    @AfterEach
    public void tearDown() {
        userRepository.deleteAll();
        eventRepository.deleteAll();
        voteRepository.deleteAll();
    }

    @Test
    void shouldQueryVoteRecordsBetweenTime() throws Exception {
        mockMvc.perform(get("/rs/vote?startTime=2018-03-10 10:00:00&endTime=2020-01-01 10:00:00"))
                .andExpect(jsonPath("$[0].voteNum",is(5)))
                .andExpect(jsonPath("$[0]",hasKey("voteTime")))
                .andExpect(jsonPath("$[0].userId",is(userId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/vote?startTime=2020-01-01 10:00:00&endTime=2018-03-10 10:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindVoteRecordByVoteIdAndUserId() throws Exception {
        mockMvc.perform(get("/rs/voteRecord")
                .param("voteId",voteId)
                .param("userId",userId))
                .andExpect(jsonPath("$.voteNum",is(5)))
                .andExpect(jsonPath("$",hasKey("voteTime")))
                .andExpect(jsonPath("$.userId",is(userId)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/voteRecord")
                .param("voteId","45")
                .param("userId",userId))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/rs/voteRecord")
                .param("voteId",voteId)
                .param("userId","67"))
                .andExpect(status().isBadRequest());
    }
}
