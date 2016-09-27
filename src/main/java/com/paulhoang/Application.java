package com.paulhoang;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.netflix.hystrix.HystrixThreadPoolProperties;
import com.paulhoang.commands.PostGeneratedDataCommand;
import com.paulhoang.config.ApplicationConfiguration;
import com.paulhoang.data.PostData;
import com.paulhoang.hystrix.HyxtrixMetricsStream;
import com.squareup.okhttp.*;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import spark.ModelAndView;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.time.temporal.Temporal;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import static spark.Spark.*;

/**
 * Created by paul on 22/11/2015.
 */
public class Application {

    final public static Logger LOG = LoggerFactory.getLogger(Application.class);

    private static HystrixCommandGroupKey commandGroupKey;
    private static ApplicationConfiguration appConfig;


    // home.mustache file is in resources/templates directory
    //to get hystrix info go to: ~/Dropbox/dev/MySharesReborn/Hystrix/hystrix-dashboard
    //run ../gradlew jettyRun
    //point to http://localhost:5551/sharesUK/hystrix.stream
    private static final MustacheTemplateEngine mustacheTemplateEngine = new MustacheTemplateEngine();

    private static boolean runningGeneration;

    public static void main(final String[] args) {

        LOG.info("Starting app... arguments: {}", ArrayUtils.toString(args));
        appConfig = loadConfiguration(args);
        commandGroupKey = HystrixCommandGroupKey.Factory.asKey(appConfig.getProfile());

        port(appConfig.getPort());

        //hystrix metrix endpoint
        get(appConfig.getApplicationContext() + "/hystrix.stream", (rq, rs) -> {
            HyxtrixMetricsStream.handleRequest(rq.raw(), rs.raw());
            return null;
        });

        final Map<String, String> homePageMap = new HashMap<>();
        homePageMap.put("generatePage", appConfig.getGenerate());
        get(appConfig.getApplicationContext(), (rq, rs) -> new ModelAndView(homePageMap, "home.mustache"), mustacheTemplateEngine);

        final Map<String, Object> generatePageMap = new HashMap<>();
        generatePageMap.put("disabledGenerator", !canRunGenerator());
        generatePageMap.put("country", appConfig.getProfile());
        generatePageMap.put("generate", appConfig.getGenerate());
        generatePageMap.put("generatorStatus", runningGeneration ? "Running" : "Not Running");

        get(appConfig.getGenerate(), (rq, rs) -> new ModelAndView(generatePageMap, "generate.mustache"), mustacheTemplateEngine);
        get(appConfig.getApplicationContext() + "/canGenerate", (rq, rs) -> canRunGenerator());

        post(appConfig.getGenerate(), (rq, rs) -> {

                    LOG.info(rq.attributes().toString());
                    LOG.info(rq.params().toString());
                    int durationInMinutes = 0;
                    int updatesPerSec = 0;
                    int concurrentThreads = 0;
                    try{
                        durationInMinutes = Integer.parseInt(rq.queryParams("duration"));
                        updatesPerSec = Integer.parseInt(rq.queryParams("updatesPerSec"));
                        concurrentThreads = Integer.parseInt(rq.queryParams("concurrentThreads"));
                    }
                    catch (final NumberFormatException nfe)
                    {
                        LOG.info("Error in parsing rq param {}", nfe);
                    }

                    List<PostData> postDataList = createPostData(durationInMinutes, updatesPerSec);
                    createAndStartThreads(postDataList, concurrentThreads);
                    rs.redirect(appConfig.getApplicationContext());
                    return null;
                }
        );

        LOG.info("Access the application on: localhost:{}{}", appConfig.getPort(), appConfig.getApplicationContext());
    }

    private static void createAndStartThreads(final List<PostData> postDataList, final int concurrentThreads) {
        for(int i = 0; i < concurrentThreads; i++){
            final PostThread thread = new PostThread(postDataList);
            thread.start();
        }
    }

    private static ApplicationConfiguration loadConfiguration(String[] args) {
        final Yaml yaml = new Yaml();
        InputStream configInputStream;
        if (args.length < 1) {
            LOG.info("Usage: java -jar application.jar {profile} i.e. uk/us/jp");
            LOG.info("Using application.yml profile");
            configInputStream = Application.class.getClassLoader()
                    .getResourceAsStream("config/application.yml");
        }
        else
        {
            configInputStream = Application.class.getClassLoader()
                    .getResourceAsStream("config/" + args[0] + ".yml");
        }

        return yaml.loadAs(configInputStream, ApplicationConfiguration.class);
    }


    private static boolean canRunGenerator() {
        return !runningGeneration;
    }

    private static List<PostData> createPostData(final int loopDuration, final int updatesPerSec) {
        LOG.info("Loop duration in mins {}, updatesPerSec {}", loopDuration, updatesPerSec);

        LocalDateTime startTime = LocalDateTime.now();
        LocalDateTime endTime =  startTime.plus(loopDuration, ChronoUnit.MINUTES);

        final List<PostData> postDataList = new ArrayList<>();
        while(startTime.isBefore(endTime)){
            long i = 1L;
            while(i <= updatesPerSec) {
                final LocalDateTime threadEndTime = startTime.plus(i * (1000L / updatesPerSec), ChronoUnit.MILLIS);
                LOG.info("creating thread start time {} end time {}", startTime, threadEndTime);
                postDataList.add(new PostData(startTime, threadEndTime));
                i++;
            }
            startTime = startTime.plus(1L, ChronoUnit.SECONDS);
        }

        return postDataList;
    }

}
