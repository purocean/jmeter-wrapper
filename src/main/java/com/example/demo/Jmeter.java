package com.example.demo;

import org.apache.jmeter.JMeter;
import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.testelement.TestPlan;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;

public class Jmeter {
    public void createJmeter() {
        // jemter 引擎
        StandardJMeterEngine standardJMeterEngine = new StandardJMeterEngine();
        // 设置不适用gui的方式调用jmeter
        System.setProperty(JMeter.JMETER_NON_GUI, "true");
        // 设置jmeter.properties文件，我们将jmeter文件存放在resources中，通过classload
        String path = TaskService.class.getClassLoader().getResource("jmeter.properties").getPath();
        File jmeterPropertiesFile = new File(path);
        if (jmeterPropertiesFile.exists()) {
            JMeterUtils.loadJMeterProperties(jmeterPropertiesFile.getPath());
            HashTree testPlanTree = new HashTree();
            // 创建测试计划
            TestPlan testPlan = new TestPlan("Create JMeter Script From Java Code");
            // 创建http请求收集器
            HTTPSamplerProxy examplecomSampler = createHTTPSamplerProxy();
            // 创建循环控制器
            LoopController loopController = createLoopController();
            // 创建线程组
            ThreadGroup threadGroup = createThreadGroup();
            // 线程组设置循环控制
            threadGroup.setSamplerController(loopController);
            // 将测试计划添加到测试配置树
            HashTree threadGroupHashTree = testPlanTree.add(testPlan, threadGroup);
            // 将http请求采样器添加到线程组下
            threadGroupHashTree.add(examplecomSampler);
            //增加结果收集

            String file = "stresstest.csv";
            ResultCollector jMeterSummarizer = buildJMeterSummarizer(file);
            testPlanTree.add(testPlan, jMeterSummarizer);

            // 配置jmeter
            standardJMeterEngine.configure(testPlanTree);
            // 运行
            standardJMeterEngine.run();

            ReportGenerator generator = null;
            try {
                generator = new ReportGenerator(file, null);
                generator.generate();
            } catch (ConfigurationException | GenerationException e) {
                e.printStackTrace();
            }
        }
    }

    public static ResultCollector buildJMeterSummarizer(String logFileName) {
        // TODO 删除老文件

        // add Summarizer output to get progress in stdout:
        Summariser summariser = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summariser = new Summariser(summariserName);
        }
        assert summariser != null;
        summariser.setEnabled(true);
        // Store execution results into a .csv file
        ResultCollector resultCollector = new ResultCollector(summariser);
        resultCollector.setFilename(logFileName);
        resultCollector.setEnabled(true);
//         resultCollector.setErrorLogging(true);
        return resultCollector;
    }

    /**
     * 创建线程组
     *
     * @return
     */
    public static ThreadGroup createThreadGroup() {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Example Thread Group");
        threadGroup.setNumThreads(1);
        threadGroup.setRampUp(0);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setScheduler(true);
        threadGroup.setDuration(5);
        threadGroup.setDelay(0);
        return threadGroup;
    }

    /**
     * 创建循环控制器
     *
     * @return
     */
    public static LoopController createLoopController() {
        // Loop Controller
        LoopController loopController = new LoopController();
        loopController.setLoops(-1);
        loopController.setContinueForever(true);
        loopController.setProperty(TestElement.TEST_CLASS, LoopController.class.getName());
        loopController.initialize();
        return loopController;
    }

    /**
     * 创建http采样器
     *
     * @return
     */
    public static HTTPSamplerProxy createHTTPSamplerProxy() {
        HeaderManager headerManager = new HeaderManager();
        headerManager.setProperty("Content-Type", "multipart/form-data");
        HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();
        httpSamplerProxy.setDomain("www.baidu.com");
        httpSamplerProxy.setPort(80);
        httpSamplerProxy.setPath("/");
        httpSamplerProxy.setMethod("GET");
        httpSamplerProxy.setConnectTimeout("5000");
        httpSamplerProxy.setUseKeepAlive(true);
        httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
        httpSamplerProxy.setHeaderManager(headerManager);
        return httpSamplerProxy;
    }
}
