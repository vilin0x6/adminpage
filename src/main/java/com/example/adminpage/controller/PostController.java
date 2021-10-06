package com.example.adminpage.controller;

import com.example.adminpage.model.SearchParam;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api")
public class PostController {

    @PostMapping("/postMethod")
    public SearchParam postMethod(@RequestBody SearchParam searchParam) {
        return searchParam;
    }

    @PutMapping("/")
    public void put() {

    }

    @PatchMapping("/")
    public void patch() {

    }
}
