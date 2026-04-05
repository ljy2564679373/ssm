package edu.java.eams.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/notice")
public class NoticeController {

    /**
     * 公告管理列表（暂为静态页面，占位，后续可接数据库）
     */
    @GetMapping("/list")
    public String list() {
        return "noticeList";
    }
}

