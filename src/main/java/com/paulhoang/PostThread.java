package com.paulhoang;

import com.netflix.hystrix.HystrixCommandGroupKey;
import com.paulhoang.commands.PostGeneratedDataCommand;
import com.paulhoang.data.PostData;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by paul on 19/09/2016.
 */
public class PostThread extends Thread {

    private static final Logger LOG = LoggerFactory.getLogger(PostThread.class);

    private List<PostData> postDataList;

    public PostThread(final List<PostData> postDataList)
    {
        this.postDataList = postDataList;
    }

    @Override
    public void run() {
        for(final PostData postData : postDataList) {
            LOG.info("posting data from {} to {}", postData.getStartTime(), postData.getEndTime());
            boolean executed = false;
            do {
                LocalDateTime now = LocalDateTime.now();
                if (now.isAfter(postData.getStartTime()) && now.isBefore(postData.getEndTime())) {
                    LOG.info("Running thread: {}", Thread.currentThread().getName());
                    sendData(Thread.currentThread().getName(), now);
                    executed = true;
                }

                if (now.isAfter(postData.getEndTime())) {
                    LOG.info("cancelling thread");
                    executed = true;
                }
            }
            while (!executed);
        }
    }


    private void sendData(String name, LocalDateTime now){
        new PostGeneratedDataCommand(HystrixCommandGroupKey.Factory.asKey("group"), name, now).execute();
    }
}
