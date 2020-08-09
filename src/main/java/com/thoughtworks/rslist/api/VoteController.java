package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Entity.UserEntity;
import com.thoughtworks.rslist.Entity.VoteEntity;
import com.thoughtworks.rslist.Service.RsService;
import com.thoughtworks.rslist.Service.UserService;
import com.thoughtworks.rslist.Service.VoteService;
import com.thoughtworks.rslist.domain.Vote;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

@RestController
public class VoteController {
    //final使用构造器注入IoC容器
    private final RsService rsService;
    private final UserService userService;
    private final VoteService voteService;
    private final DateTimeFormatter dateTimeFormat = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public VoteController(RsService rsService, UserService userService,
                          VoteService voteService) {
        this.rsService = rsService;
        this.userService = userService;
        this.voteService = voteService;
    }

    @PostMapping("/rs/{rsEventId}/vote")
    @Transactional
    public ResponseEntity checkVoteStatusForUser (@PathVariable int rsEventId, @RequestBody Vote vote) {
        int voteUserId = Integer.valueOf(vote.getUserId());
        if(!rsService.isEventExists(rsEventId) || !userService.isUserExists(voteUserId) || !isValidForVote(vote)) {
            return ResponseEntity.badRequest().build();
        }
        VoteEntity voteRecord = Vote.voteEntityBuilder(vote);
        voteRecord.setUser(userService.getUserInfoById(voteUserId));
        voteRecord.setEvent(rsService.getEventById(rsEventId));
        VoteEntity newVote = voteService.addOrChangeVoteRecord(voteRecord);

        EventEntity voteEvent = rsService.getEventById(rsEventId);
        voteEvent.setVoteNum(voteEvent.getVoteNum() + vote.getVoteNum());
        rsService.addOrSaveEvent(voteEvent);

        UserEntity voteUser = userService.getUserInfoById(voteUserId);
        voteUser.setVote(voteUser.getVote() - vote.getVoteNum());
        userService.addOrSaveUser(voteUser);

        return ResponseEntity.status(HttpStatus.CREATED).body(newVote.getId());
    }

    private boolean isValidForVote(Vote vote) {
        String userId = vote.getUserId();
        UserEntity checkedUser = userService.getUserInfoById(Integer.valueOf(userId));
        if(checkedUser.getVote() > vote.getVoteNum()) {
            return true;
        }
        return false;
    }

    @GetMapping("/rs/vote/searchByTime")
    public ResponseEntity<List<VoteEntity>> checkVoteRecordWithinTime(
            @RequestParam String startTime, @RequestParam String endTime) {
        LocalDateTime startDate = LocalDateTime.parse(startTime,dateTimeFormat);
        LocalDateTime endDate = LocalDateTime.parse(endTime,dateTimeFormat);
        if(startDate.isAfter(endDate)) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(voteService.findVoteRecordWithinTime(startDate,endDate));
    }

    //On class demo 1
    @GetMapping("/rs/vote")
    @Transactional
    public ResponseEntity<List<VoteEntity>> checkVoteRecordByEventIdAndUserId(@RequestParam String userId,
                                                           @RequestParam String eventId,
                                                         @RequestParam(required = false) String pageIndex) {
        if(!userService.isUserExists(Integer.valueOf(userId)) ||
            !rsService.isEventExists(Integer.valueOf(eventId))) {
            return ResponseEntity.badRequest().build();
        }
        List<VoteEntity> chosenVote;
        if(pageIndex == null) {
            chosenVote = voteService.findVoteRecordsByUserIdAndEventId(userId,eventId);
        }else{
            Pageable pageable = PageRequest.of(Integer.valueOf(pageIndex)-1,5);
            chosenVote = voteService.findVoteRecordsByUserIdAndEventId(userId,eventId,pageable);
        }
        return ResponseEntity.ok(chosenVote);
    }
}
