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
        return taskService.status();
    }

    @PostMapping("/api/run")
    //public void run (@RequestBody List<Task> tasks) {
    public void run () {
        Task task = new Task();

        task.method = "GET";
        task.url = "http://www.google.cn/?ab=c";
        task.duration = 1;
        task.data = "";
        task.threads = 1;

        Task task2 = new Task();
        task2.method = "GET";
        task2.url = "http://www.baidu.com/?ab=c";
        task2.duration = 1;
        task2.data = "";
        task2.threads = 1;

        ArrayList<Task> tasks = new ArrayList<Task>();

        tasks.add(task);
        tasks.add(task2);

        taskService.run(tasks);
    }
}
