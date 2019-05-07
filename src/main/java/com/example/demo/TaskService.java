package com.example.demo;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class TaskService {
    private StandardJMeterEngine engine;

    @Async
    public void run (List<Task> tasks) {
        if (this.engine != null && this.engine.isActive()) {
            return;
        }

        HashTree testPlanTree = new HashTree();

        tasks.forEach(task -> {
            TestPlan testPlan = new TestPlan(task.url);
            // 创建http请求收集器
            HTTPSamplerProxy examplecomSampler = Jmeter.createHTTPSamplerProxy(task.method, task.url, task.data);
            // 创建循环控制器
            LoopController loopController = Jmeter.createLoopController();
            // 创建线程组
            ThreadGroup threadGroup = Jmeter.createThreadGroup(task.duration, task.threads);
            // 线程组设置循环控制
            threadGroup.setSamplerController(loopController);
            // 将测试计划添加到测试配置树
            HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
            // 将http请求采样器添加到线程组下
            threadGroupHashTree.add(examplecomSampler);
            // 日志记录
            testPlanTree.add(testPlan, Jmeter.buildJMeterSummarizer());
        });

        this.engine = new Jmeter().createJmeter(testPlanTree);

        this.engine.run();

        Jmeter.gen();
    }

    public void stop () {
        this.engine.askThreadsToStop();
    }

    public Status status() {
        Status status = new Status();

        status.active = this.engine != null && this.engine.isActive();

        HashMap<String, Result> result = new HashMap<String, Result>();

        Jmeter.result.forEach((k, v) -> {
            Result r = new Result();

            List<SampleResult> s = v.stream().filter(SampleResult::isSuccessful).collect(Collectors.toList());

            r.total = v.size();
            r.success = s.size();

            if (r.success > 0) {
                r.sst = s.stream().mapToLong(SampleResult::getStartTime).min().getAsLong();
                r.set = s.stream().mapToLong(SampleResult::getStartTime).max().getAsLong();

                r.est = s.stream().mapToLong(SampleResult::getStartTime).min().getAsLong();
                r.eet = s.stream().mapToLong(SampleResult::getStartTime).max().getAsLong();

                r.duration = r.eet - r.sst;

                r.fail = r.total - r.success;
                r.rps = s.size() / ((r.set - r.sst) / 1000.0);
                r.tps = s.size() / ((r.eet - r.sst) / 1000.0);
                r.bytes = s.stream().mapToLong(SampleResult::getBytesAsLong).sum();
                r.sendBytes = s.stream().mapToLong(SampleResult::getSentBytes).sum();
                r.maxThreads = s.stream().mapToLong(SampleResult::getGroupThreads).max().getAsLong();

                r.maxTime = s.stream().mapToLong(x -> x.getEndTime() - x.getStartTime()).max().getAsLong();
                r.minTime = s.stream().mapToLong(x -> x.getEndTime() - x.getStartTime()).min().getAsLong();
                r.meanTime = s.stream().mapToLong(x -> x.getEndTime() - x.getStartTime()).average().getAsDouble();
            }

            result.put(k, r);
        });

        status.result = result;

        return status;
    }
}
