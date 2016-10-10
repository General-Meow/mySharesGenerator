package com.paulhoang.commands;

import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.paulhoang.data.Data;
import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.time.LocalDateTime;

/**
 * Created by paul on 22/11/2015.
 */
public class PostGeneratedDataCommand extends HystrixCommand<String> {

    private static final Logger LOG = LoggerFactory.getLogger(PostGeneratedDataCommand.class);
    private Gson gson = new Gson();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private String name;
    private double price;


    public PostGeneratedDataCommand(final HystrixCommandGroupKey group, String name, double price) {
        super(group);
        this.name = name;
        this.price = price;
    }

    //TODO overload the constructor to take a json payload which the run method will use to post

    @Override
    protected String run()  {
        final OkHttpClient restfulClient = new OkHttpClient();

        Data data = new Data(this.name, this.price);
        RequestBody body = RequestBody.create(JSON, gson.toJson(data));
        LOG.info("About to post");
        Request request = new Request.Builder()
                .url("http://localhost:5555/collect")
                .post(body)
                .build();
        try {
            Response response = restfulClient.newCall(request).execute();
            String bodyresp = response.body().string();
            LOG.info("Got response {}", bodyresp);
            return bodyresp;
        } catch (IOException e) {
            System.out.println("io exception...");
            e.printStackTrace();
        }
        return "failed";
    }

//    @Override
//    protected String getFallback() {
//        return "Failed - got fallback";
//    }

}
