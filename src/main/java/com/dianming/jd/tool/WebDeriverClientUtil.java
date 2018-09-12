package com.dianming.jd.tool;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.openqa.selenium.remote.RemoteWebDriver;

import java.net.URL;


public class WebDeriverClientUtil {
    private WebDriver webDriver;

    public double beginPost(String skuId) {
        try {
            if (this.webDriver == null) {
                ChromeOptions chromeOptions = new ChromeOptions();
                chromeOptions.addArguments(new String[]{"--headless", "window-size=720x800", "--hide-scrollbars", "blink-settings=imagesEnabled=false"});

                DesiredCapabilities capabilities = DesiredCapabilities.chrome();
                capabilities.setCapability("chromeOptions", chromeOptions);
                this.webDriver = new RemoteWebDriver(new URL("http://127.0.0.1:4444/wd/hub"), capabilities);
            }

            long tim1 = System.currentTimeMillis();
            this.webDriver.get("https://item.jd.com/" + skuId + ".html");
            WebElement elementName = this.webDriver.findElement(By.cssSelector("div.dd span.p-price span.price"));
            String text = elementName.getText();

            long tim2 = System.currentTimeMillis();
            System.out.println("Get " + skuId + ", time:" + (tim2 - tim1) + ", price:" + text);
            return Double.parseDouble(text);
        } catch (Exception e) {
        }
        return 0.0;
    }

    public static void main(String[] args) {
    }
}