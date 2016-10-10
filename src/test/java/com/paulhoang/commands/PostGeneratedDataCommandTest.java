package com.paulhoang.commands;

import au.com.dius.pact.consumer.ConsumerPactTest;
import au.com.dius.pact.consumer.Pact;
import au.com.dius.pact.consumer.PactVerification;
import au.com.dius.pact.consumer.dsl.PactDslWithProvider;
import au.com.dius.pact.model.PactFragment;
import com.squareup.okhttp.*;
import jdk.nashorn.internal.ir.annotations.Reference;
import org.junit.runner.RunWith;
import org.mockito.internal.runners.JUnit44RunnerImpl;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;

/**
 * Created by paul on 29/09/2016.
 */
public class PostGeneratedDataCommandTest extends ConsumerPactTest {

    private OkHttpClient client = new OkHttpClient();

    @Override
    @Pact(consumer = "SharesGenerator", provider = "SharesCollector")
    protected PactFragment createFragment(PactDslWithProvider builder) {
        Map<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "text/html;charset=utf-8");

        return builder
//                .given("test state") // NOTE: Using provider states are optional, you can leave it out
                .uponReceiving("a request posting share data")
                    .path("/collect")
                    .method("POST")
                .willRespondWith()
                    .status(200)
                    .headers(headers)
                    .body("hello")
                    .toFragment();


    }

    @Override
    protected String providerName() {
        return "SharesCollector";
    }

    @Override
    protected String consumerName() {
        return "SharesGenerator";
    }

    @PactVerification(fragment = "SharesGenerator")
    @Override
    protected void runTest(String url) throws IOException {

        RequestBody body = RequestBody.create(MediaType.parse("application/json; charset=utf-8"), "");

        Request request = new Request.Builder()
                .url(url + "/collect")
                .post(body)
                .build();

        Response response = client.newCall(request).execute();
        String bodyresp = response.body().string();
        assertEquals(200, response.code());
        assertEquals("hello", bodyresp);

    }

}