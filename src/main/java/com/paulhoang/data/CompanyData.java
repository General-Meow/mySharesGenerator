package com.paulhoang.data;

import java.math.BigDecimal;

/**
 * Created by paul on 08/12/2016.
 */
public class CompanyData {

    private String name;
    private BigDecimal startingPrice;
    private Trend trend;
    private float randomness;

    public CompanyData(String name, Trend trend, float randomness) {
        this.name = name;
        this.trend = trend;
        this.randomness = randomness;
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

    public float getRandomness() {
        return randomness;
    }

    public void setRandomness(float randomness) {
        this.randomness = randomness;
    }
}
