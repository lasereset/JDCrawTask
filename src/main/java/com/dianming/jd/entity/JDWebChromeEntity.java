package com.dianming.jd.entity;


public class JDWebChromeEntity {
    private String skuId;

    private double price;

    private double purchasingPrice;


    public JDWebChromeEntity() {
    }


    public JDWebChromeEntity(String skuId, double price, double purchasingPrice) {
        this.skuId = skuId;
        this.price = price;
        this.purchasingPrice = purchasingPrice;
    }

    public String getSkuId() {
        return this.skuId;
    }

    public void setSkuId(String skuId) {
        this.skuId = skuId;
    }

    public double getPrice() {
        return this.price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getPurchasingPrice() {
        return this.purchasingPrice;
    }

    public void setPurchasingPrice(double purchasingPrice) {
        this.purchasingPrice = purchasingPrice;
    }
}