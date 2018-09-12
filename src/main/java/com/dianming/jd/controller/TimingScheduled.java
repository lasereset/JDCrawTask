package com.dianming.jd.controller;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianming.jd.entity.JDWebChromeEntity;
import com.dianming.jd.service.JDService;
import com.dianming.jd.tool.*;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map;

import static com.dianming.jd.service.JDService.RESULT_URL;

@Component
@Transactional
@RestController
@RequestMapping({"/api/"})
public class TimingScheduled {
    @Resource
    private JDService jdService;
    public static boolean EXECUTE_PRICE_TASK = false;

    private static final int taskCount = 2;

    public static int queryJdSkuIdCounts(Integer beginId) {
        HttpRequestText httpRequestText = new HttpRequestText();
        httpRequestText.setHeader("beginId", String.valueOf(beginId));
        String result = httpRequestText.request(RESULT_URL + "jd/chromeApi/queryJdSkuIdCounts.do");
        int count = Fusion.isEmpty(result) ? 0 : Integer.parseInt(result);
        return count;
    }


    public static QueryResponse<Integer> queryJdSkuIdByPage(int startIndex, int endIndex, Integer beginId) {
        HttpRequestText httpRequestText = new HttpRequestText();
        httpRequestText.setHeader("startIndex", String.valueOf(startIndex));
        httpRequestText.setHeader("endIndex", String.valueOf(endIndex));
        httpRequestText.setHeader("beginId", String.valueOf(beginId));
        String result = httpRequestText.request(RESULT_URL + "jd/chromeApi/queryJdSkuIds.do");
        QueryResponse<Integer> queryResponse = (QueryResponse) Fusion.getObject(result, QueryResponse.class);
        return queryResponse;
    }

    public static ApiResponse updateJdCommodityPrice(String jdWebChromeDatas) {
        HttpRequestText httpRequestText = new HttpRequestText();
        httpRequestText.setHeader("datas", jdWebChromeDatas);
        String result = httpRequestText.request(RESULT_URL + "jd/chromeApi/updateCommodityData.do");
        ApiResponse apiResponse = (ApiResponse) Fusion.getObject(result, ApiResponse.class);
        return apiResponse;
    }


    @org.springframework.scheduling.annotation.Scheduled(cron = "0 00 03 * * ?")
    public void executejdrealprice() {
        executejdrealprice(0);
    }


    private static int current_task_count = 2;

    private void beginExecute(QueryResponse<Integer> response, int taskId) {
        if ((response == null) || (Fusion.isEmpty(response.getItems()))) {
            System.out.println("线程" + taskId + "没有数据！");
            return;
        }


        List<List<Integer>> skuIdLists = new ArrayList();
        List<Integer> items = response.getItems();
        int allNum = items.size();
        System.out.println("线程" + taskId + ">>>共有任务：" + allNum + "个");


        Map<String, Double> skuPurchasingPriceMap = new java.util.HashMap();
        if (allNum > 100) {
            for (int i = 0; i < allNum; i += 100) {
                if (i + 100 > allNum) {
                    skuIdLists.add(items.subList(i, allNum));
                } else {
                    skuIdLists.add(items.subList(i, i + 100));
                }
            }
            for (List<Integer> skuIdList : skuIdLists) {
                syncPurchasingPrice(skuIdList, skuPurchasingPriceMap);
            }
        } else {
            syncPurchasingPrice(items, skuPurchasingPriceMap);
        }

        List<JDWebChromeEntity> jdWebChromeEntities = new ArrayList();
        WebDeriverClientUtil webDeriverClientUtil = new WebDeriverClientUtil();

        for (Integer skuId : items) {
            if (!EXECUTE_PRICE_TASK) {
                System.out.println(">>>>>>结束爬取任务<<<<<<");
                return;
            }
            allNum--;
            Double purchasingPrice = skuPurchasingPriceMap.get(String.valueOf(skuId));
            if ((purchasingPrice != null) && (purchasingPrice.doubleValue() != 0.0D)) {

                double price = webDeriverClientUtil.beginPost(String.valueOf(skuId));
                jdWebChromeEntities.add(new JDWebChromeEntity(String.valueOf(skuId), price, purchasingPrice.doubleValue()));
                System.out.println("线程" + taskId + ">>>剩余爬取任务：" + allNum + "个===>京东ID:" + skuId + "----官网价:" + price + "----建议价:" + purchasingPrice);
            }
        }
        if (current_task_count == 0) {
            EXECUTE_PRICE_TASK = false;
        }

        updateJdCommodityPrice(com.alibaba.fastjson.JSON.toJSONString(jdWebChromeEntities));
    }

    private void syncPurchasingPrice(List<Integer> skuIds, Map<String, Double> skuPurchasingPriceMap) {
        ApiResponse priceResponse = this.jdService.bizPriceSellPriceGet((Integer[]) skuIds.toArray(new Integer[0]));
        if (priceResponse.getCode() == 200) {
            QueryResponse priceQueryResponse = (QueryResponse) priceResponse;
            JSONArray priceArray = (JSONArray) priceQueryResponse.getItems();
            if (!Fusion.isEmpty(priceArray))
                for (int p = 0; p < priceArray.size(); p++) {
                    JSONObject priceObject = priceArray.getJSONObject(p);
                    skuPurchasingPriceMap.put(priceObject.getString("skuId"), Double.valueOf(priceObject.getDoubleValue("price")));
                }
        }
    }

    public void executejdrealprice(final int beginId) {
        if (EXECUTE_PRICE_TASK) {
            return;
        }
        System.out.println(IntelliDate.format(new Date()) + ">>>>>>开始爬取任务从<<<<<<" + beginId + "开始！");
        EXECUTE_PRICE_TASK = true;

        int allNum = queryJdSkuIdCounts(Integer.valueOf(beginId));
        if (allNum == 0) {
            return;
        }

        System.out.println("共有任务：" + allNum + "个");


        if (allNum <= 100) {
            current_task_count = 1;
            System.out.println("只有一个线程！！！！！！！！！！！！！！！");
            QueryResponse<Integer> response = queryJdSkuIdByPage(0, allNum, Integer.valueOf(beginId));
            beginExecute(response, 1);
        } else {
            int size = allNum / 2;
            current_task_count = 2;
            final int first_startIndex = 0;
            final int first_endIndex = size;
            final int second_startIndex = first_endIndex + 1;
            final int second_endIndex = allNum;

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startCrawData(1, first_startIndex, first_endIndex, beginId);
                }
            }).start();

            new Thread(new Runnable() {
                @Override
                public void run() {
                    startCrawData(2, second_startIndex, second_endIndex, beginId);
                }
            }).start();
        }
    }


    private void startCrawData(int taskId, int index1, int index2, int beginId) {
        for (int i = index1; i <= index2; i += 500) {
            if (!EXECUTE_PRICE_TASK) {
                return;
            }
            int endIndex = i + 500;
            if (i + 500 >= index2) {
                current_task_count -= 1;
                endIndex = index2;
            }

            System.out.println("从" + i + "到" + endIndex + "分配任务！");
            QueryResponse<Integer> response = queryJdSkuIdByPage(i, 500, Integer.valueOf(beginId));
            beginExecute(response, taskId);
        }
    }


    @ResponseBody
    @RequestMapping({"stopCurrentTask.do"})
    public ApiResponse stopCurrentTask() {
        EXECUTE_PRICE_TASK = false;
        return new ApiResponse(200, "停止成功！");
    }


    @ResponseBody
    @RequestMapping({"beginSomeTask.do"})
    public ApiResponse beginSomeTask(String beginId) {
        int beginid = 0;
        if (!Fusion.isEmpty(beginId)) {
            try {
                beginid = Integer.parseInt(beginId);
            } catch (NumberFormatException e) {
            }
        }
        executejdrealprice(beginid);
        return new ApiResponse(200, "开始成功！");
    }
}