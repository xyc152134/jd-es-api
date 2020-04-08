package com.xyc.jdesapi.controller;

import com.xyc.jdesapi.service.JDinfoService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.List;
import java.util.Map;

@RestController
public class JDInfoController {
    @Autowired
    private JDinfoService jDinfoService;
    @GetMapping("/parse/{keyword}")
    public Boolean parse(@PathVariable("keyword") String keyword) throws Exception {
        return  jDinfoService.parseContent(keyword);
    }
    @GetMapping("/search/{keyword}/{pageNo}/{pageSize}")
    public List<Map<String,Object>> searchByPage(@PathVariable("keyword") String keyword,
                                                 @PathVariable("pageNo")  int pageNo,
                                                 @PathVariable("pageSize")  int pageSize) throws IOException {
        return jDinfoService.searchByPageHight(keyword,pageNo,pageSize);

    }
}
