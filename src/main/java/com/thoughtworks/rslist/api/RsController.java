package com.thoughtworks.rslist.api;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Arrays;
import java.util.List;

@RestController
public class RsController {
    private List<String> rsList = Arrays.asList("第一条事件", "第二条事件", "第三条事件");

    @GetMapping("/rs/list/{index}")
    public String getSpecialEvents(@PathVariable int index) {
        if(index < 1 || index > rsList.size()) {
            return "Cannot reach corresponding data";
        }
        return rsList.get(index - 1);
    }

    @GetMapping("rs/list")
    public String getSpecialRange(@RequestParam(required = false) Integer start,
                                  @RequestParam(required = false) Integer end) {
        if(start == null || end == null) {
            return rsList.toString();
        }
        if(start >= end) {
            return "Your range input doesn't make sense, please recheck.";
        }
        return rsList.subList(start-1,end).toString();
    }
}
