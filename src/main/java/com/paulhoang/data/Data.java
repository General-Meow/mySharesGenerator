package com.paulhoang.data;

/**
 * Created by paul on 30/09/2016.
 */
public class Data {
    private String name;
    private double price;

    public Data(String name, double price) {
        this.name = name;
        this.price = price;
    }

    public String getName() {
        return name;
    }

    public double getPrice() {
        return price;
    }

}
