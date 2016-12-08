package com.paulhoang;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.paulhoang.config.ApplicationConfiguration;
import com.paulhoang.data.CompanyData;
import com.paulhoang.data.PostData;
import com.paulhoang.data.ShareData;
import com.paulhoang.hystrix.HyxtrixMetricsStream;
import org.apache.commons.lang.ArrayUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.yaml.snakeyaml.Yaml;
import spark.ModelAndView;
import spark.Request;
import spark.Response;
import spark.template.mustache.MustacheTemplateEngine;

import java.io.FileReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import static spark.Spark.*;

/**
 * Created by paul on 22/11/2015.
 */
public class Application {

    final public static Logger LOG = LoggerFactory.getLogger(Application.class);

    private static HystrixCommandGroupKey commandGroupKey;
    private static ApplicationConfiguration appConfig;
    private static List<CompanyData> companyData;
    private static Map<String, BigDecimal> lastCompanyPrices;
    private static final Type REVIEW_TYPE = new TypeToken<List<CompanyData>>() { }.getType();

    // home.mustache file is in resources/templates directory
    //to get hystrix info go to: ~/Dropbox/dev/MySharesReborn/Hystrix/hystrix-dashboard
    //run ../gradlew jettyRun
    //point to http://localhost:5551/sharesUK/hystrix.stream
    private static final MustacheTemplateEngine mustacheTemplateEngine = new MustacheTemplateEngine();

    public static boolean runningGeneration = false;

    public static void main(final String[] args) throws Exception {

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
        generatePageMap.put("country", appConfig.getProfile());
        generatePageMap.put("generateAction", appConfig.getGenerate());
        generatePageMap.put("generatorStatus", runningGeneration ? "Running" : "Not Running");

        get(appConfig.getGenerate(), (rq, rs) -> new ModelAndView(generatePageMap, "generate.mustache"), mustacheTemplateEngine);
        post(appConfig.getGenerate(), (rq, rs) -> handleGenerateRequest(rq, rs));

        final Map<String, Object> generateAdvancedPageMap = new HashMap<>();
        generateAdvancedPageMap.put("country", appConfig.getProfile());
        generateAdvancedPageMap.put("generateAdvancedAction", appConfig.getGenerateAdvanced());
        generateAdvancedPageMap.put("generatorStatus", runningGeneration ? "Running" : "Not Running");
        get(appConfig.getGenerateAdvanced(), (rq, rs) -> new ModelAndView(generatePageMap, "generate-advanced.mustache"), mustacheTemplateEngine);
        post(appConfig.getGenerate(), (rq, rs) -> handleGenerateRequest(rq, rs));

        get(appConfig.getApplicationContext() + "/isRunning", (rq, rs) -> isRunning());

        companyData = getCompanyDataFromFile();
        lastCompanyPrices = generateLastCompanyPrices(companyData);

        LOG.info("Access the application on: localhost:{}{}", appConfig.getPort(), appConfig.getApplicationContext());
    }

    static Object handleGenerateRequest(Request rq, Response rs) {
        LOG.info(rq.attributes().toString());
        LOG.info(rq.params().toString());

        int durationInMinutes = 1;
        int updatesPerSec = 1;
        int concurrentThreads = 1;
        try{
            if(StringUtils.isNotEmpty(rq.queryParams("duration"))) {
                durationInMinutes = Integer.parseInt(rq.queryParams("duration"));
            }
            if(StringUtils.isNotEmpty(rq.queryParams("updatesPerSec"))){
                updatesPerSec = Integer.parseInt(rq.queryParams("updatesPerSec"));
            }
            if(StringUtils.isNotEmpty(rq.queryParams("concurrentThreads"))) {
                concurrentThreads = Integer.parseInt(rq.queryParams("concurrentThreads"));
            }
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


    private static boolean isRunning() {
        return runningGeneration;
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
                final List<ShareData> shareDataList = generateShareDataForCompanies();
                postDataList.add(new PostData(startTime, threadEndTime, shareDataList));
                i++;
            }
            startTime = startTime.plus(1L, ChronoUnit.SECONDS);
        }

        return postDataList;
    }

    private static Map<String, BigDecimal> generateLastCompanyPrices(List<CompanyData> companies){
        Map<String, BigDecimal> lastCompanyPrices = new HashMap<>();
        for(final CompanyData company : companies)
        {
            lastCompanyPrices.put(company.getName(), company.getStartingPrice());
        }
        return lastCompanyPrices;
    }

    private static List<CompanyData> getCompanyDataFromFile() throws Exception{
        List<CompanyData> data = new ArrayList<>();

        Gson gson = new Gson();
        JsonReader reader = new JsonReader(new InputStreamReader(Application.class.getClassLoader()
                .getResourceAsStream("config/companies.json")));
        data = gson.fromJson(reader, REVIEW_TYPE); // contains the whole reviews list
        LOG.info("my {}", data);
        return data;
    }

    private static List<ShareData> generateShareDataForCompanies(){
        final List<ShareData> data = new ArrayList<>();

        for(final CompanyData company : companyData)
        {
            LOG.info("data {} {} {}", company.getName(), company.getStartingPrice(), company.getTrend());
            //final ShareData shareData = new ShareData();//lastCompanyPrices.get(company.getName()) + BigDecimal.ONE;
            //data.add(shareData);
        }

        return data;
    }
}
