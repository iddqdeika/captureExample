package com.iddqdeika.springlearn.rest;

import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class RootRestController {

    @RequestMapping("/")
    public String index(){
        return "Hello Spring";
    }
}
