package com.paulhoang.commands;

import com.google.gson.Gson;
import com.netflix.hystrix.HystrixCommand;
import com.netflix.hystrix.HystrixCommandGroupKey;
import com.paulhoang.Application;
import com.paulhoang.config.ApplicationConfiguration;
import com.paulhoang.data.ShareData;
import com.squareup.okhttp.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.util.List;

/**
 * Created by paul on 22/11/2015.
 */
public class PostGeneratedDataCommand extends HystrixCommand<String> {

    private static final Logger LOG = LoggerFactory.getLogger(PostGeneratedDataCommand.class);
    private Gson gson = new Gson();
    public static final MediaType JSON = MediaType.parse("application/json; charset=utf-8");

    private List<ShareData> shareData;


    public PostGeneratedDataCommand(final HystrixCommandGroupKey group, final List<ShareData> shareData) {
        super(group);
        this.shareData = shareData;
    }

    //TODO overload the constructor to take a json payload which the run method will use to post

    @Override
    protected String run()  {
        final OkHttpClient restfulClient = new OkHttpClient();

        RequestBody body = RequestBody.create(JSON, gson.toJson(shareData));

        ApplicationConfiguration config = Application.appConfig;
        LOG.info("about to push to {}, {}", config.getPushEndpoint(), config.getProfile());
        Request request = new Request.Builder()
                .url(config.getPushEndpoint())
                .post(body)
                .build();
        try {
            Response response = restfulClient.newCall(request).execute();
            String bodyresp = response.body().string();
            LOG.info("Got response {}", bodyresp);
            return bodyresp;
        } catch (IOException e) {
            LOG.error("io exception...", e);
        }
        return "failed";
    }

    @Override
    protected String getFallback() {
        return "Failed - got fallback";
    }

}
