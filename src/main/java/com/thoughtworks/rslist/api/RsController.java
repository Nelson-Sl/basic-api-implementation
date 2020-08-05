package com.thoughtworks.rslist.api;

import com.thoughtworks.rslist.domain.User;
import com.thoughtworks.rslist.exception.CommonError;
import com.thoughtworks.rslist.exception.InvalidIndexInputException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.thoughtworks.rslist.domain.HotEvents;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
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
    public ResponseEntity<List<HotEvents>> getSpecialRange(@RequestParam(required = false) Integer start,
                                  @RequestParam(required = false) Integer end) {
        if(start == null || end == null) {
            return ResponseEntity.ok(rsList);
        }else if(start > end) {
            ResponseEntity.status(HttpStatus.BAD_REQUEST);
        }
        return ResponseEntity.ok(rsList.subList(start-1,end));
    }

    @PostMapping("/rs/addEvent")
    public ResponseEntity addHotEvent(@RequestBody HotEvents newEvent) {
        System.out.println(newEvent.getEventName());
        System.out.println(newEvent.getKeyWord());
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

    @ExceptionHandler(InvalidIndexInputException.class)
    public ResponseEntity exceptionHandler(InvalidIndexInputException iie) {
        CommonError commonError = new CommonError();
        commonError.setError(iie.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(commonError);
    }
}
