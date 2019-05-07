package com.example.demo;

import org.apache.jmeter.control.LoopController;
import org.apache.jmeter.engine.StandardJMeterEngine;
import org.apache.jmeter.protocol.http.control.HeaderManager;
import org.apache.jmeter.protocol.http.sampler.HTTPSamplerProxy;
import org.apache.jmeter.report.config.ConfigurationException;
import org.apache.jmeter.report.dashboard.GenerationException;
import org.apache.jmeter.report.dashboard.ReportGenerator;
import org.apache.jmeter.reporters.ResultCollector;
import org.apache.jmeter.reporters.Summariser;
import org.apache.jmeter.samplers.SampleEvent;
import org.apache.jmeter.samplers.SampleResult;
import org.apache.jmeter.testelement.TestElement;
import org.apache.jmeter.threads.ThreadGroup;
import org.apache.jmeter.util.JMeterUtils;
import org.apache.jorphan.collections.HashTree;

import java.io.File;
import java.util.*;

public class Jmeter {
    public static String logFileName = "stresstest.csv";
    public static String exportHtmlDir = "report-output";

    public static HashMap<String, List<SampleResult>> result = new HashMap<String, List<SampleResult>>();

    public static class MyResultCollector extends ResultCollector {

        public MyResultCollector(Summariser summer) {
            super(summer);
        }

        @Override
        public void sampleOccurred(SampleEvent e) {
            super.sampleOccurred(e);
            SampleResult r = e.getResult();

            String key = r.getUrlAsString();
            if (result.containsKey(key)) {
                result.get(key).add(r);
            } else {
                ArrayList<SampleResult> list = new ArrayList<SampleResult>();
                list.add(r);
                result.put(key, list);
            }
        }
    }

    public StandardJMeterEngine createJmeter(HashTree testPlanTree) {
        // 清理日志文件
        File file = new File(logFileName);
        if (file.exists()) file.delete();
        File dir = new File(exportHtmlDir);
        if (dir.exists()) delete(dir.getAbsolutePath());

        // 清理结果
        result = new HashMap<>();

        StandardJMeterEngine standardJMeterEngine = new StandardJMeterEngine();
        // 配置jmeter
        standardJMeterEngine.configure(testPlanTree);

        return standardJMeterEngine;
    }

    public static void delete(String path) {
        File f = new File(path);
        if(f.isDirectory()){//如果是目录，先递归删除
            String[] list=f.list();
            for (int i=0; i < list.length; i++) {
                delete(path+"//"+list[i]);//先删除目录下的文件
            }
        }
        f.delete();
    }

    public static void gen() {
        ReportGenerator generator = null;
        try {
            String file = logFileName;
            generator = new ReportGenerator(file, null);
            generator.generate();
        } catch (ConfigurationException | GenerationException e) {
            e.printStackTrace();
        }
    }

    public static ResultCollector buildJMeterSummarizer() {
        // add Summarizer output to get progress in stdout:
        Summariser summariser = null;
        String summariserName = JMeterUtils.getPropDefault("summariser.name", "summary");
        if (summariserName.length() > 0) {
            summariser = new Summariser(summariserName);
        }
        assert summariser != null;
        summariser.setEnabled(true);
        // Store execution results into a .csv file
        ResultCollector resultCollector = new MyResultCollector(summariser);
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
    public static ThreadGroup createThreadGroup(long duration, int threads) {
        ThreadGroup threadGroup = new ThreadGroup();
        threadGroup.setName("Example Thread Group");
        threadGroup.setNumThreads(threads);
        threadGroup.setRampUp(0);
        threadGroup.setProperty(TestElement.TEST_CLASS, ThreadGroup.class.getName());
        threadGroup.setScheduler(true);
        threadGroup.setDuration(duration);
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
    public static HTTPSamplerProxy createHTTPSamplerProxy(String method, String urlStr, String data) {
        try {
            //URL url = new URL(urlStr);

            HeaderManager headerManager = new HeaderManager();
            HTTPSamplerProxy httpSamplerProxy = new HTTPSamplerProxy();

            if (method.equals("POST")) {
                headerManager.setProperty("Content-Type", "application/x-www-form-urlencoded");
                httpSamplerProxy.parseArguments(data);
            }

            httpSamplerProxy.setPath(urlStr);
            //httpSamplerProxy.setDomain(url.getHost());
            //httpSamplerProxy.setPort(url.getPort());
            //httpSamplerProxy.setPath(url.getPath());
            //httpSamplerProxy.setProtocol(url.getProtocol());
            httpSamplerProxy.setMethod(method);
            httpSamplerProxy.setConnectTimeout("5000");
            httpSamplerProxy.setUseKeepAlive(true);
            httpSamplerProxy.setProperty(TestElement.TEST_CLASS, HTTPSamplerProxy.class.getName());
            httpSamplerProxy.setHeaderManager(headerManager);
            return httpSamplerProxy;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return null;
    }
}
