package com.paulhoang.data;

import java.math.BigDecimal;

/**
 * Created by paul on 08/12/2016.
 */
public class CompanyData {

    private String name;
    private BigDecimal startingPrice;
    private Trend trend;
    private float trendFlipChance;

    public CompanyData(String name, Trend trend, float trendFlipChance) {
        this.name = name;
        this.trend = trend;
        this.trendFlipChance = trendFlipChance;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public BigDecimal getStartingPrice() {
        return startingPrice;
    }

    public void setStartingPrice(BigDecimal startingPrice) {
        this.startingPrice = startingPrice;
    }

    public Trend getTrend() {
        return trend;
    }

    public void setTrend(Trend trend) {
        this.trend = trend;
    }

    public float getTrendFlipChance() {
        return trendFlipChance;
    }

    public void setTrendFlipChance(float trendFlipChance) {
        this.trendFlipChance = trendFlipChance;
    }

    @Override
    public String toString() {
        return "CompanyData{" +
                "name='" + name + '\'' +
                ", startingPrice=" + startingPrice +
                ", trend=" + trend +
                ", trendFlipChance=" + trendFlipChance +
                '}';
    }
}
