package com.example.demo;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ApiController {
    @GetMapping("/api/status")
    public Status status () {
        Status status = new Status();
        status.count = 0;
        status.done = 0;
        return status;
    }
}

class Status {
    public Integer count;
    public Integer done;
}