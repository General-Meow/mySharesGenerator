package com.paulhoang.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;

/**
 * Created by paul on 21/09/2016.
 */
public class PostData {

    private LocalDateTime startTime;
    private LocalDateTime endTime;

    public PostData(final LocalDateTime startTime, final LocalDateTime endTime){
        this.startTime = startTime;
        this.endTime = endTime;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }
}
