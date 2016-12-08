package com.paulhoang.data;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDateTime;
import java.util.List;

/**
 * Created by paul on 21/09/2016.
 */
public class PostData {

    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private List<ShareData> companySharePrices;

    public PostData(final LocalDateTime startTime, final LocalDateTime endTime, final List<ShareData> companySharePrices){
        this.startTime = startTime;
        this.endTime = endTime;
        this.companySharePrices = companySharePrices;
    }

    public LocalDateTime getStartTime() {
        return startTime;
    }

    public LocalDateTime getEndTime() {
        return endTime;
    }

    public List<ShareData> getCompanySharePrices() {
        return companySharePrices;
    }
}
