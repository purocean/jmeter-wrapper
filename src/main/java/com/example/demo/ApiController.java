package com.example.demo;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class ApiController {
    @Autowired
    private TaskService taskService;

    @GetMapping("/api/status")
    public Status status () {
        return taskService.status();
    }

    @RequestMapping("/api/test")
    public String test (HttpServletRequest req, @RequestParam Map<String,String> allRequestParams) throws IOException {
        String x = req.getMethod() + allRequestParams.entrySet().stream().map(e -> e.getKey() + "=" + e.getValue()).collect(Collectors.joining("&"));
        System.out.println(x);

        return "ok";
    }

    @PostMapping("/api/stop")
    public void stop () {
        taskService.stop();
    }

    @PostMapping("/api/run")
    public void run (@RequestBody List<Task> tasks) {
    //public void run () {
        Task task = new Task();

        //task.method = "GET";
        //task.url = "http://www.google.cn/?ab=c";
        //task.duration = 1;
        //task.data = "";
        //task.threads = 1;
        //
        //Task task2 = new Task();
        //task2.method = "GET";
        //task2.url = "http://www.baidu.com/?ab=c";
        //task2.duration = 1;
        //task2.data = "";
        //task2.threads = 1;
        //
        //ArrayList<Task> tasks = new ArrayList<Task>();
        //
        //tasks.add(task);
        //tasks.add(task2);

        taskService.run(tasks);
    }
}
