package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;

@RestController
public class ApiController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/api/status")
    public Status status () {
        Status status = new Status();
        status.count = 0;
        status.done = 0;
        return status;
    }

    @PostMapping("/api/run")
    public void run () {
        Task task = new Task();

        task.name = "谷歌";
        task.method = "GET";
        task.url = "http://www.google.cn/?ab=c";
        task.duration = 1;
        task.data = "";

        ArrayList<Task> tasks = new ArrayList<Task>();

        tasks.add(task);

        taskService.run(tasks);
    }
}
