package com.crawler.web;

import com.crawler.config.auth.LoginUser;
import com.crawler.config.auth.dto.SessionUser;
import com.crawler.service.posts.PostsService;
import com.crawler.web.dto.PostsResponseDto;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import javax.servlet.http.HttpSession;

@RequiredArgsConstructor
@Controller
public class TestController {

    private final PostsService postsService;
    private final HttpSession httpSession;



    @GetMapping("/test")
    public String index(Model model, @LoginUser SessionUser user) {
        model.addAttribute("posts", postsService.findAllDesc());

        if (user != null) {
            model.addAttribute("userName", user.getName());
        }
        return "test";
    }

    @GetMapping("test/posts/save")
    public String postsSave() {
        return "test-posts-save";
    }

    @GetMapping("test/posts/update/{id}")
    public String postsUpdate(@PathVariable Long id, Model model) {
        PostsResponseDto dto = postsService.findById(id);
        model.addAttribute("post", dto);

        return "test-posts-update";
    }
}