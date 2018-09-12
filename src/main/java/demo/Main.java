package demo;

import com.alibaba.fastjson.JSON;
import com.dianming.jd.controller.TimingScheduled;
import com.dianming.jd.tool.QueryResponse;

import java.util.List;


public class Main {
    public static void main(String[] args) {
        QueryResponse response = TimingScheduled.queryJdSkuIdByPage(0, 100, Integer.valueOf(0));
        List<String> skuIds = response.getItems();
        System.out.println(JSON.toJSONString(skuIds.toArray(new Integer[0])));
    }
}