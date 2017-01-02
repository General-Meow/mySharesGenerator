package com.paulhoang.data;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.Date;

/**
 * Created by paul on 30/10/2016.
 */
public class ShareData implements Serializable{
    private String name;
    private String code;
    private BigDecimal price;
    private Date datetime;

    public ShareData(String name, String code, BigDecimal price, Date datetime) {
        this.name = name;
        this.code = code;
        this.price = price;
        this.datetime = datetime;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public Date getDatetime() {
        return datetime;
    }

    public void setDatetime(Date datetime) {
        this.datetime = datetime;
    }

    @Override
    public String toString() {
        return "ShareData{" +
                "name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", price=" + price +
                ", datetime=" + datetime +
                '}';
    }
}
