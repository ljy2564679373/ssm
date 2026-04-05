package edu.java.eams.controller;

import edu.java.eams.domain.PostLevel;
import edu.java.eams.service.PostLevelService;
import edu.java.eams.comm.RetJson;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@Controller
@RequestMapping("/postlevel")
public class PostLevelController {
    @Autowired
    private PostLevelService postLevelService;

    @GetMapping("/list")
    public String list() {
        return "redirect:/postlevel/page";
    }

    @GetMapping("/add")
    public String addForm() {
        return "postLevelAdd";
    }

    @PostMapping("/add")
    public String add(PostLevel postLevel) {
        postLevelService.addPostLevel(postLevel);
        return "redirect:/postlevel/list";
    }

    @GetMapping("/edit/{id}")
    public String editForm(@PathVariable("id") Long id, Model model) {
        model.addAttribute("postLevel", postLevelService.getPostLevelById(id));
        return "postLevelEdit";
    }

    @PostMapping("/edit")
    public String edit(PostLevel postLevel) {
        postLevelService.updatePostLevel(postLevel);
        return "redirect:/postlevel/list";
    }

    @PostMapping("/editJson")
    @ResponseBody
    public RetJson<String> editJson(PostLevel postLevel) {
        try {
            postLevelService.updatePostLevel(postLevel);
            return RetJson.success("编辑成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("编辑失败：" + e.getMessage());
        }
    }

    @GetMapping("/delete/{id}")
    public String delete(@PathVariable("id") Long id) {
        postLevelService.deletePostLevel(id);
        return "redirect:/postlevel/list";
    }

    /**
     * AJAX 删除岗位，避免整页跳转
     */
    @PostMapping("/deleteJson/{id}")
    @ResponseBody
    public RetJson<String> deleteJson(@PathVariable("id") Long id) {
        try {
            postLevelService.deletePostLevel(id);
            return RetJson.success("删除成功");
        } catch (Exception e) {
            e.printStackTrace();
            return RetJson.error("删除失败：" + e.getMessage());
        }
    }

    @GetMapping("/page")
    public String page(
        @RequestParam(value = "name", required = false) String name,
        @RequestParam(value = "pageNum", defaultValue = "1") Integer pageNum,
        @RequestParam(value = "pageSize", defaultValue = "10") Integer pageSize,
        Model model) {
        if (pageNum == null || pageNum < 1) pageNum = 1;
        if (pageSize == null || pageSize < 1) pageSize = 10;
        int start = (pageNum - 1) * pageSize;
        List<PostLevel> postLevels = postLevelService.getPostLevelsByCondition(name, start, pageSize);
        int total = postLevelService.countPostLevelsByCondition(name);
        model.addAttribute("postLevels", postLevels);
        model.addAttribute("total", total);
        model.addAttribute("pageNum", pageNum);
        model.addAttribute("pageSize", pageSize);
        model.addAttribute("name", name);
        return "postLevelList";
    }
} 
