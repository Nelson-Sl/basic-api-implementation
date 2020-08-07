package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Entity.VoteEntity;
import com.thoughtworks.rslist.Repository.EventRepository;
import com.thoughtworks.rslist.Repository.UserRepository;
import com.thoughtworks.rslist.Repository.VoteRepository;
import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.domain.Vote;
import com.thoughtworks.rslist.exception.InvalidIndexInputException;
import com.thoughtworks.rslist.exception.InvalidRequestParamException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thoughtworks.rslist.domain.HotEvents;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {
    private List<HotEvents> rsList = Stream.of(
            new HotEvents("第一条事件","无分类",
                    "1",10),
            new HotEvents("第二条事件","无分类",
                    "2",10),
            new HotEvents("第三条事件","无分类",
                    "3",10))
            .collect(Collectors.toList());

    //final使用构造器注入IoC容器
    private final EventRepository eventRepository;
    private final UserRepository userRepository;
    private final VoteRepository voteRepository;

    public RsController(EventRepository eventRepository, UserRepository userRepository,
        VoteRepository voteRepository) {
        this.eventRepository = eventRepository;
        this.userRepository = userRepository;
        this.voteRepository = voteRepository;
    }



    @GetMapping("/rs/list/{index}")
    public ResponseEntity getSpecialEvents(@PathVariable int index) throws InvalidIndexInputException {
        if(index > rsList.size()) {
            throw new InvalidIndexInputException("invalid index");
        }
        EventEntity event = eventRepository.findById(index).get();
        return ResponseEntity.ok(event);
    }



    @GetMapping("/rs/list")
    public ResponseEntity getSpecialRange(@RequestParam(required = false) Integer start,
                                  @RequestParam(required = false) Integer end) throws InvalidRequestParamException {
        if(start == null || end == null) {
            return ResponseEntity.ok(eventRepository.findAll());
        }
        if(isInvalid(start,end)) {
            throw new InvalidRequestParamException("invalid request param");
        }
        return ResponseEntity.ok(eventRepository.findAll().subList(start,end));
    }

    private boolean isInvalid(Integer start, Integer end) {
        return start >= end || start < 0 || start > rsList.size() || end < 0;
    }

    @PostMapping("/rs/addEvent")
    public ResponseEntity addHotEvent(@Validated @RequestBody HotEvents newEvent) {
        String userId = newEvent.getUserId();
        if(!userRepository.existsById(Integer.valueOf(userId))) {
            return ResponseEntity.badRequest().build();
        }
        EventEntity eventData = EventEntity.builder()
                .eventName(newEvent.getEventName())
                .keyWord(newEvent.getKeyWord())
                .userId(newEvent.getUserId())
                .voteNum(newEvent.getVoteNum())
                .build();
        EventEntity eventDataSaved = eventRepository.save(eventData);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(eventDataSaved.getId()));
    }

    @PostMapping("/rs/alterEvent")
    public ResponseEntity alterHotEvent(@RequestParam String indexStr,
                              @RequestParam(required = false) String eventName,
                              @RequestParam(required = false) String keyWord) {
        int index = Integer.parseInt(indexStr);
        if(eventName != null) {
            rsList.get(index - 1).setEventName(eventName);
        }
        if(keyWord != null) {
            rsList.get(index - 1).setKeyWord(keyWord);
        }
        return ResponseEntity.ok().build();
    }

    @PostMapping("/rs/deleteEvent")
    public ResponseEntity deleteHotEvent(@RequestParam String indexStr) {
        int index = Integer.parseInt(indexStr);
        return ResponseEntity.ok(rsList.remove(index-1));
    }

    @PatchMapping("/rs/{rsEventId}")
    public ResponseEntity updateEventIfUserExists(@PathVariable int rsEventId,
                                                  @RequestParam(required=false) String eventName,
                                                  @RequestParam(required=false) String keyWord) {
        EventEntity eventChosen = eventRepository.findById(rsEventId).get();
        String userIdChosen = eventChosen.getUserId();
        if(!userRepository.existsById(Integer.valueOf(userIdChosen))){
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        if(eventName != null) {
            eventChosen.setEventName(eventName);
        }if(keyWord != null) {
            eventChosen.setKeyWord(keyWord);
        }
        EventEntity changedEvent = eventRepository.save(eventChosen);
        return ResponseEntity.status(HttpStatus.OK).body(changedEvent.getUserId());
    }

    @PostMapping("/rs/vote/{rsEventId}")
    @Transactional
    public ResponseEntity checkVoteStatusForUser (@PathVariable int rsEventId, @RequestBody Vote vote) {
        if(eventIdNotExist(rsEventId) || userIdNotExist(vote) || !isValidForVote(vote)) {
            return ResponseEntity.badRequest().build();
        }
        VoteEntity voteRecord = VoteEntity.builder()
                .voteNum(vote.getVoteNum())
                .voteTime(vote.getVoteTime())
                .userId(vote.getUserId()).build();
        voteRepository.save(voteRecord);

        EventEntity voteEvent = eventRepository.findById(rsEventId).get();
        voteEvent.setVoteNum(voteEvent.getVoteNum() + vote.getVoteNum());
        eventRepository.save(voteEvent);

        UserEntity voteUser = userRepository.findById(Integer.valueOf(vote.getUserId())).get();
        voteUser.setVote(voteUser.getVote() - vote.getVoteNum());
        userRepository.save(voteUser);

        return ResponseEntity.created(null).build();
    }

    private boolean userIdNotExist(Vote vote) {
        if(userRepository.existsById(Integer.valueOf(vote.getUserId()))) {
            return false;
        }
        return true;
    }

    private boolean eventIdNotExist(int rsEventId) {
        if(eventRepository.existsById(rsEventId)) {
            return false;
        }
        return true;
    }

    private boolean isValidForVote(Vote vote) {
        String userId = vote.getUserId();
        UserEntity checkedUser = userRepository.findById(Integer.valueOf(userId)).get();
        if(checkedUser.getVote() > vote.getVoteNum()) {
            return true;
        }
        return false;
    }
}
