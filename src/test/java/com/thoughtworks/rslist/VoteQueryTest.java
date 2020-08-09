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

import static org.hamcrest.Matchers.*;
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
        userId = mockMvc.perform(post("/user")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        event = HotEvents.builder().eventName("一个热搜事件").keyWord("无分类")
                .user(user).userId(Integer.valueOf(userId)).voteNum(10).build();
        String eventInfo = objectMapper.writeValueAsString(event);
        eventId = mockMvc.perform(post("/rs/event")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        LocalDateTime voteTime =
                LocalDateTime.of(2019, Month.MARCH,21,12,5,12);
        vote = Vote.builder().voteNum(1).voteTime(voteTime).user(user).userId(userId).eventId(eventId).build();
        String userVoteInfo = objectMapper.writeValueAsString(vote);
        voteId = mockMvc.perform(post("/rs/"+ eventId +"/vote")
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
    void getSpecialEventsFromDatabase() throws Exception {
        mockMvc.perform(get("/rs/list/" + eventId))
                .andExpect(jsonPath("$.eventName",is("一个热搜事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.id",is(Integer.valueOf(eventId))))
                .andExpect(jsonPath("$.voteNum",is(11)))
                .andExpect(status().isOk());
    }

    @Test
    void shouldQueryVoteRecordsBetweenTime() throws Exception {
        mockMvc.perform(get("/rs/vote/searchByTime?startTime=2018-03-10 10:00:00&endTime=2020-01-01 10:00:00"))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].voteNum",is(1)))
                .andExpect(jsonPath("$[0]",hasKey("voteTime")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/vote/searchByTime?startTime=2020-01-01 10:00:00&endTime=2018-03-10 10:00:00"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldFindVoteRecordByUserIdAndEventId() throws Exception {
        mockMvc.perform(get("/rs/vote")
                .param("userId",userId)
                .param("eventId",eventId))
                .andExpect(jsonPath("$",hasSize(1)))
                .andExpect(jsonPath("$[0].voteNum",is(1)))
                .andExpect(jsonPath("$[0]",hasKey("voteTime")))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/vote")
                .param("userId","45")
                .param("eventId",eventId))
                .andExpect(status().isBadRequest());

        mockMvc.perform(get("/rs/vote")
                .param("userId",eventId)
                .param("eventId","67"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldShowPageRecordInMultiplePages() throws Exception {
        for(int i = 0; i < 7; i++) {
            LocalDateTime voteTime =
                    LocalDateTime.of(2019, Month.MARCH,21,12,5,12);
            vote = Vote.builder().voteNum(1).voteTime(voteTime).userId(userId).eventId(eventId).build();
            String userVoteInfo = objectMapper.writeValueAsString(vote);

            mockMvc.perform(post("/rs/"+ eventId +"/vote")
                    .content(userVoteInfo).contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isCreated())
                    .andReturn().getResponse().getContentAsString();
        }

        mockMvc.perform(get("/rs/vote")
                .param("userId",userId)
                .param("eventId",eventId)
                .param("pageIndex","1"))
                .andExpect(jsonPath("$",hasSize(5)))
                .andExpect(jsonPath("$[0].voteNum",is(1)))
                .andExpect(jsonPath("$[1].voteNum",is(1)))
                .andExpect(jsonPath("$[2].voteNum",is(1)))
                .andExpect(jsonPath("$[3].voteNum",is(1)))
                .andExpect(jsonPath("$[4].voteNum",is(1)))
                .andExpect(status().isOk());

        mockMvc.perform(get("/rs/vote")
                .param("userId",userId)
                .param("eventId",eventId)
                .param("pageIndex","2"))
                .andExpect(jsonPath("$",hasSize(3)))
                .andExpect(jsonPath("$[0].voteNum",is(1)))
                .andExpect(jsonPath("$[1].voteNum",is(1)))
                .andExpect(jsonPath("$[2].voteNum",is(1)))
                .andExpect(status().isOk());
    }
}
