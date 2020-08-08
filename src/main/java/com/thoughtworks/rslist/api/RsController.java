package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.Entity.EventEntity;
import com.thoughtworks.rslist.Service.RsService;
import com.thoughtworks.rslist.Service.UserService;
import com.thoughtworks.rslist.exception.InvalidIndexInputException;
import com.thoughtworks.rslist.exception.InvalidRequestParamException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thoughtworks.rslist.domain.HotEvents;
import org.springframework.web.bind.annotation.*;

@RestController
public class RsController {
    //final使用构造器注入IoC容器
    private final RsService rsService;
    private final UserService userService;

    public RsController(RsService rsService, UserService userService) {
        this.rsService = rsService;
        this.userService = userService;
    }

    @GetMapping("/rs/list/{index}")
    public ResponseEntity getSpecialEvents(@PathVariable int index) throws InvalidIndexInputException {
        if(!rsService.isEventExists(index)) {
            throw new InvalidIndexInputException("invalid index");
        }
        EventEntity event = rsService.getEventById(index);
        return ResponseEntity.ok(event);
    }

    @GetMapping("/rs/list")
    public ResponseEntity getSpecialRange(@RequestParam(required = false) Integer start,
                                  @RequestParam(required = false) Integer end) throws InvalidRequestParamException {
        if(start == null || end == null) {
            return ResponseEntity.ok(rsService.getAllEvents());
        }
        if(isInvalid(start,end)) {
            throw new InvalidRequestParamException("invalid request param");
        }
        return ResponseEntity.ok(rsService.getEventWithinRanges(start,end));
    }

    private boolean isInvalid(Integer start, Integer end) {
        return start >= end || start < 0 || start > rsService.getEventCount()|| end < 0;
    }

    @PostMapping("/rs/addEvent")
    public ResponseEntity addHotEvent(@Validated @RequestBody HotEvents newEvent) {
        String userId = newEvent.getUserId();
        if(!userService.isUserExists(Integer.valueOf(userId))) {
            return ResponseEntity.badRequest().build();
        }
        EventEntity eventData = HotEvents.eventEntityBuilder(newEvent);
        EventEntity eventDataSaved = rsService.addOrSaveEvent(eventData);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(eventDataSaved.getId()));
    }

    @PostMapping("/rs/alterEvent")
    public ResponseEntity alterHotEvent(@RequestParam String indexStr,
                              @RequestParam(required = false) String eventName,
                              @RequestParam(required = false) String keyWord) {
        int eventId = Integer.valueOf(indexStr);
        if(!rsService.isEventExists(eventId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        EventEntity changingEvent = rsService.getEventById(eventId);
        if(eventName != null) {
            changingEvent.setEventName(eventName);
        }
        if(keyWord != null) {
            changingEvent.setKeyWord(keyWord);
        }
        EventEntity eventChanged = rsService.addOrSaveEvent(changingEvent);
        return ResponseEntity.ok("Successfully change event, id:" + eventChanged.getId());
    }

    @PostMapping("/rs/deleteEvent")
    public ResponseEntity deleteHotEvent(@RequestParam String indexStr) {
        int eventId = Integer.valueOf(indexStr);
        if(!rsService.isEventExists(eventId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        rsService.deleteEventById(eventId);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @PatchMapping("/rs/{rsEventId}")
    public ResponseEntity updateEventIfUserExists(@PathVariable int rsEventId,
                                                  @RequestParam(required=false) String eventName,
                                                  @RequestParam(required=false) String keyWord) {
        if(!rsService.isEventExists(rsEventId)) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }else if(!userService.isUserExists(Integer.valueOf(rsService.getEventById(rsEventId).getUserId()))) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).build();
        }
        EventEntity eventChosen = rsService.getEventById(rsEventId);
        if(eventName != null) {
            eventChosen.setEventName(eventName);
        }if(keyWord != null) {
            eventChosen.setKeyWord(keyWord);
        }
        EventEntity changedEvent = rsService.addOrSaveEvent(eventChosen);
        return ResponseEntity.status(HttpStatus.OK).body(changedEvent.getUserId());
    }
}
