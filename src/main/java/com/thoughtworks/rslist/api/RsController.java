package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.InvalidIndexInputException;
import com.thoughtworks.rslist.exception.InvalidRequestParamException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thoughtworks.rslist.domain.HotEvents;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {
    private List<HotEvents> rsList = Stream.of(
            new HotEvents("第一条事件","无分类",
                    new User("Tony",28,"Male", "tony@sina.cn","17458957454")),
            new HotEvents("第二条事件","无分类",
                    new User("Mark",25,"Male", "mark@sina.cn","17458957455")),
            new HotEvents("第三条事件","无分类",
                    new User("Jenny",27,"Female", "jenny@sina.cn","17458957456")))
            .collect(Collectors.toList());;


    @GetMapping("/rs/list/{index}")
    public ResponseEntity getSpecialEvents(@PathVariable int index) throws InvalidIndexInputException {
        if(index > rsList.size()) {
            throw new InvalidIndexInputException("invalid index");
        }
        return ResponseEntity.ok(rsList.get(index-1));
    }

    @GetMapping("/rs/list")
    public ResponseEntity getSpecialRange(@RequestParam(required = false) Integer start,
                                  @RequestParam(required = false) Integer end) throws InvalidRequestParamException {
        if(start == null || end == null) {
            return ResponseEntity.ok(rsList);
        }
        if(isInvalid(start,end)) {
            throw new InvalidRequestParamException("invalid request param");
        }
        return ResponseEntity.ok(rsList.subList(start-1,end));
    }

    private boolean isInvalid(Integer start, Integer end) {
        return start >= end || start < 0 || start > rsList.size() || end < 0;
    }

    @PostMapping("/rs/addEvent")
    public ResponseEntity addHotEvent(@Validated @RequestBody HotEvents newEvent) {
        rsList.add(newEvent);
        return ResponseEntity.status(HttpStatus.CREATED).body(String.valueOf(rsList.size()-1));
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
}
