package com.example.aws_practice;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class AwsRestController {

    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }
}
