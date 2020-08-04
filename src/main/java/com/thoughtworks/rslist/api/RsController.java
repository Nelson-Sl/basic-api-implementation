package com.thoughtworks.rslist.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.thoughtworks.rslist.domain.HotEvents;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
public class RsController {
    private List<HotEvents> rsList = Stream.of(new HotEvents("第一条事件","无分类"),
            new HotEvents("第二条事件","无分类"),
            new HotEvents("第三条事件","无分类")).collect(Collectors.toList());

    @GetMapping("/rs/list/{index}")
    public HotEvents getSpecialEvents(@PathVariable int index) {
        return rsList.get(index - 1);
    }

    @GetMapping("/rs/list")
    public List<HotEvents> getSpecialRange(@RequestParam(required = false) Integer start,
                                  @RequestParam(required = false) Integer end) {
        if(start == null || end == null) {
            return rsList;
        }
        return rsList.subList(start-1,end);
    }

    @PostMapping("/rs/addEvent")
    public void addHotEvent(@RequestBody HotEvents newEvent) {
        System.out.println(newEvent.getEventName());
        System.out.println(newEvent.getKeyWord());
        rsList.add(newEvent);
    }

    @PostMapping("/rs/alterEvent")
    public void alterHotEvent(@RequestParam String indexStr,
                              @RequestParam(required = false) String eventName,
                              @RequestParam(required = false) String keyWord) {
        int index = Integer.parseInt(indexStr);
        if(eventName != null) {
            rsList.get(index - 1).setEventName(eventName);
        }
        if(keyWord != null) {
            rsList.get(index - 1).setKeyWord(keyWord);
        }
    }

    @PostMapping("/rs/deleteEvent")
    public void deleteHotEvent(@RequestParam String indexStr) {
        int index = Integer.parseInt(indexStr);
        rsList.remove(index-1);
    }


}
