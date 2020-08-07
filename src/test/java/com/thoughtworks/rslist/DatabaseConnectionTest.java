package com.thoughtworks.rslist;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Entity.VoteEntity;
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
import org.springframework.test.web.servlet.ResultMatcher;

import javax.persistence.Column;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

import static org.hamcrest.Matchers.hasKey;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.hamcrest.Matchers.is;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@AutoConfigureMockMvc
public class DatabaseConnectionTest {
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
    }

    @Test
    void shouldAddOneUserToRepository() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        List<UserEntity> userEntities = userRepository.findAll();
        assertEquals(1,userEntities.size());
        assertEquals("Mark",userEntities.get(0).getUserName());
    }

    @Test
    void shouldGetUserInfoFromRepository() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo = objectMapper.writeValueAsString(user);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        List<UserEntity> users = userRepository.findAll();
        mockMvc.perform(get("/user/" + userId))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.userName",is("Mark")))
                .andExpect(jsonPath("$.gender",is("Male")))
                .andExpect(jsonPath("$.age",is(24)))
                .andExpect(jsonPath("$.email",is("mark@gmail.com")))
                .andExpect(jsonPath("$.phone",is("18888888888")));
    }

    @Test
    void shouldDeleteUserFromRepository() throws Exception {
        User user1 = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo1 = objectMapper.writeValueAsString(user1);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        mockMvc.perform(delete("/deleteUser/" + userId)) 
                .andExpect(status().isOk());
        List<UserEntity> userList = userRepository.findAll();
        assertEquals(0,userList.size());
    }

    @Test
    void shouldAddingEventWhenUsersHadRegistered() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo = objectMapper.writeValueAsString(user);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        HotEvents event = new HotEvents("一个热搜事件","无分类",userId,10);
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/addEvent")
                        .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isCreated());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals("一个热搜事件",eventRepository.findAll().get(0).getEventName());
    }

    @Test
    void shouldNotAddingEventWhenUsersHadNotRegistered() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo = objectMapper.writeValueAsString(user);
        mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        HotEvents event = new HotEvents("一个热搜事件","无分类","33",10);
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void shouldDeleteUserWithHisEvents() throws Exception {
        User user1 = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo1 = objectMapper.writeValueAsString(user1);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo1)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        HotEvents event = new HotEvents("一个热搜事件","无分类",userId,10);
        String eventInfo = objectMapper.writeValueAsString(event);
        mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        mockMvc.perform(delete("/deleteUser/" + userId))
                .andExpect(status().isOk());
        assertEquals(0,userRepository.findAll().size());
        assertEquals(0,eventRepository.findAll().size());
    }

    @Test
    void shouldUpdateEventInfoWithTheUserExisting() throws Exception {
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
        String patchUrl = "/rs/" +eventId+"?eventName=另一个热搜事件&keyWord=时事";
        mockMvc.perform(patch(patchUrl))
                .andExpect(status().isOk());
        assertEquals(1, eventRepository.findAll().size());
        assertEquals("另一个热搜事件",eventRepository.findAll().get(0).getEventName());
        assertEquals("时事",eventRepository.findAll().get(0).getKeyWord());
    }

    @Test
    void judgeUserCanVoteForTheEvent() throws Exception {
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

        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        Vote userVote = new Vote(5, df.format(System.currentTimeMillis()),userId);

        String userVoteInfo = objectMapper.writeValueAsString(userVote);

        mockMvc.perform(post("/rs/vote/"+eventId)
                .content(userVoteInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated());
        assertEquals(1,voteRepository.findAll().size());
        assertEquals(5, voteRepository.findAll().get(0).getVoteNum());
        assertEquals(userId,voteRepository.findAll().get(0).getUserId());
        assertEquals(15,eventRepository.findById(Integer.valueOf(eventId)).get().getVoteNum());
        assertEquals(5, userRepository.findById(Integer.valueOf(userId)).get().getVote());

        Vote anotherUserVote = new Vote(11, df.format(System.currentTimeMillis()),userId);
        String anotherUserVoteInfo = objectMapper.writeValueAsString(anotherUserVote);
        mockMvc.perform(post("/rs/vote/"+eventId)
                .content(anotherUserVoteInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Vote voteWithNotExitUserId = new Vote(5, df.format(System.currentTimeMillis()),"33");
        String voteInfoWithNotExitUserId = objectMapper.writeValueAsString(voteWithNotExitUserId);
        mockMvc.perform(post("/rs/vote/"+eventId)
                .content(voteInfoWithNotExitUserId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());

        Vote voteWithNotExistRsEventId = new Vote(5, df.format(System.currentTimeMillis()),userId);
        String voteInfoWithNotExistRsEventId = objectMapper.writeValueAsString(voteWithNotExistRsEventId);
        mockMvc.perform(post("/rs/vote/"+33)
                .content(voteInfoWithNotExistRsEventId).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isBadRequest());
    }

    @Test
    void getSpecialEventsFromDatabase() throws Exception {
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
        mockMvc.perform(get("/rs/list/" + eventId))
                .andExpect(jsonPath("$.eventName",is("一个热搜事件")))
                .andExpect(jsonPath("$.keyWord",is("无分类")))
                .andExpect(jsonPath("$.id",is(Integer.valueOf(eventId))))
                .andExpect(jsonPath("$.voteNum",is(10)))
                .andExpect(jsonPath("$",not(hasKey("userId"))))
                .andExpect(status().isOk());
    }

    @Test
    void getSpecialRangeOfEventsFromDatabase() throws Exception {
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

        HotEvents event2 = new HotEvents("第二个热搜事件","无分类",userId,10);
        String eventInfo2 = objectMapper.writeValueAsString(event2);
        String eventId2 = mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo2).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        HotEvents event3 = new HotEvents("第三个热搜事件","无分类",userId,10);
        String eventInfo3 = objectMapper.writeValueAsString(event3);
        String eventId3 = mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo3).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(get("/rs/list"))
                .andExpect(jsonPath("$[0].eventName",is("一个热搜事件")))
                .andExpect(jsonPath("$[0].keyWord",is("无分类")))
                .andExpect(jsonPath("$[0].id",is(Integer.valueOf(eventId))))
                .andExpect(jsonPath("$[0].voteNum",is(10)))
                .andExpect(jsonPath("$[0]",not(hasKey("userId"))))
                .andExpect(jsonPath("$[1].eventName",is("第二个热搜事件")))
                .andExpect(jsonPath("$[1].keyWord",is("无分类")))
                .andExpect(jsonPath("$[1].id",is(Integer.valueOf(eventId2))))
                .andExpect(jsonPath("$[1].voteNum",is(10)))
                .andExpect(jsonPath("$[1]",not(hasKey("userId"))))
                .andExpect(jsonPath("$[2].eventName",is("第三个热搜事件")))
                .andExpect(jsonPath("$[2].keyWord",is("无分类")))
                .andExpect(jsonPath("$[2].id",is(Integer.valueOf(eventId3))))
                .andExpect(jsonPath("$[2].voteNum",is(10)))
                .andExpect(jsonPath("$[2]",not(hasKey("userId"))))
                .andExpect(status().isOk());
    }

    @Test
    void shouldAlterEventInfoIfEventExists() throws Exception {
        User user = new User("Mark",24,"Male","mark@gmail.com","18888888888",10);
        String userInfo = objectMapper.writeValueAsString(user);
        String userId = mockMvc.perform(post("/addUser")
                .content(userInfo)
                .contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();
        HotEvents event = new HotEvents("一个热搜事件","无分类",userId,10);
        String eventInfo = objectMapper.writeValueAsString(event);
        String eventId = mockMvc.perform(post("/rs/addEvent")
                .content(eventInfo).contentType(MediaType.APPLICATION_JSON))
                .andExpect(status().isCreated())
                .andReturn().getResponse().getContentAsString();

        mockMvc.perform(post("/rs/alterEvent?indexStr="+ eventId +"&eventName=特朗普辞职&keyWord=时事"))
                .andExpect(status().isOk());
        EventEntity changedEvent = eventRepository.findById(Integer.valueOf(eventId)).get();
        assertEquals("特朗普辞职", changedEvent.getEventName());
        assertEquals("时事", changedEvent.getKeyWord());
    }
}
