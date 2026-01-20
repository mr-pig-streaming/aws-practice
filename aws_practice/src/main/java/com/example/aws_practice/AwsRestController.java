package com.example.aws_practice;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
public class AwsRestController {

    @GetMapping("/")
    public String index() {
        return "Hello World!";
    }

    @PostMapping("/records")
    public ResponseEntity<Object> createRecord(@RequestBody String jsonRecords) {
        return new ResponseEntity<>("Record is successfully created", HttpStatus.CREATED);
    }

    @GetMapping("/records/{id}")
    public ResponseEntity<Object> getRecord(@PathVariable("id") String id) {
        return new ResponseEntity<>("Seeking record for id " + id, HttpStatus.FOUND);
    }

    @GetMapping("/records")
    public ResponseEntity<Object> filterRecords(@RequestParam(value = "filter", required = true) String filter) {
        return new ResponseEntity<>("Filtering based on " + filter, HttpStatus.FOUND);
    }
}
