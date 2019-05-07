package com.example.demo;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jorphan.collections.HashTree;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {
    private StandardJMeterEngine engine;

    public void run (List<Task> tasks) {
        if (this.engine != null && this.engine.isActive()) {
            return;
        }

        HashTree testPlanTree = new HashTree();

        tasks.forEach(task -> {
            TestPlan testPlan = new TestPlan(task.name);
            // 创建http请求收集器
            HTTPSamplerProxy examplecomSampler = Jmeter.createHTTPSamplerProxy(task.method, task.url, task.data);
            // 创建循环控制器
            LoopController loopController = Jmeter.createLoopController();
            // 创建线程组
            ThreadGroup threadGroup = Jmeter.createThreadGroup(task.duration);
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

        return status;
    }
}
