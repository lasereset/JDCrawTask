package com.dianming.jd.service;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.dianming.jd.entity.JDConfig;
import com.dianming.jd.tool.*;
import org.springframework.stereotype.Service;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;


@Service
public class JDService {
    private static final String RESULT_URL_1 = "http://116.62.116.53:8080/Dshop/";
    private static final String RESULT_URL_2 = "http://shop.dmrjkj.cn/";
    public static final String RESULT_URL = RESULT_URL_2;
    private static final String JD_BIZ_PRICE_SELLPRICE_GET = "biz.price.sellPrice.get";
    public static JDConfig jdConfig = null;

    private String post(String method, Map<String, Object> parameters) {
        HttpRequestText requestText = new HttpRequestText();

        requestText.setHeader("method", method);

        requestText.setHeader("app_key", jdConfig.getKey());

        requestText.setHeader("access_token", jdConfig.getToken());

        requestText.setHeader("timestamp", IntelliDate.format(new Date()));

        requestText.setHeader("format", "json");

        requestText.setHeader("v", "1.0");

        requestText.setHeader("param_json", JSON.toJSONString(parameters));

        for (Entry<String, Object> entry : parameters.entrySet()) {
            System.out.println("KEY = " + entry.getKey() + "  VALUE = " + entry.getValue());
        }

        return requestText.request("https://router.jd.com/api");
    }

    public JSONObject getJsonObjectPost(String method, Map<String, Object> parameters) {
        if (jdConfig != null) {
            if (!Fusion.isEmpty(new String[]{jdConfig.getKey(), jdConfig.getSecret()})) {
            }
        } else {
            getToken(Integer.valueOf(0));
        }

        if (jdConfig == null) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("success", Boolean.valueOf(false));
            jsonObject.put("resultCode", Integer.valueOf(500));
            jsonObject.put("resultMessage", "请求失败！");
            return jsonObject;
        }

        String result = post(method, parameters);
        System.out.println("方法名:" + method);

        JSONObject jsonObject = JSON.parseObject(result);
        if (jsonObject == null) {
            jsonObject = new JSONObject();
            jsonObject.put("success", Boolean.valueOf(false));
            jsonObject.put("resultCode", Integer.valueOf(500));
            jsonObject.put("resultMessage", "请求失败！");
            return jsonObject;
        }
        JSONObject resultJSONObject = jsonObject.getJSONObject(method.replace(".", "_") + "_response");
        if (resultJSONObject == null) {
            resultJSONObject = jsonObject.getJSONObject("errorResponse");
        }
        if (resultJSONObject == null) {
            resultJSONObject = new JSONObject();
            resultJSONObject.put("code", Integer.valueOf(1004));
        }
        Integer code = resultJSONObject.getInteger("code");
        if ((code != null) && ((code.intValue() == 1004) || (code.intValue() == 1003) || (code.intValue() == 1022) || (code.intValue() == 3025))) {
            getToken(Integer.valueOf(1));
            System.out.println("token失效!");
            return getJsonObjectPost(method, parameters);
        }
        return resultJSONObject;
    }

    private void getToken(Integer isNeedRefresh) {
        HttpRequestText requestText = new HttpRequestText();

        requestText.setHeader("isNeedRefresh", String.valueOf(isNeedRefresh));

        String tokenReulst = requestText.request(RESULT_URL + "jd/chromeApi/queryJdToken.do");

        jdConfig = (JDConfig) Fusion.getObject(tokenReulst, JDConfig.class);
    }


    public ApiResponse bizPriceSellPriceGet(Integer... skuids) {
        Map<String, Object> parameters = new HashMap();

        StringBuilder sb = new StringBuilder();
        for (Integer id : skuids) {
            sb.append(id).append(",");
        }

        parameters.put("sku", sb.toString());

        JSONObject bizPriceSellPriceGetJSONObject = getJsonObjectPost(JD_BIZ_PRICE_SELLPRICE_GET, parameters);

        if (!bizPriceSellPriceGetJSONObject.getBooleanValue("success")) {
            return new ApiResponse(bizPriceSellPriceGetJSONObject.getIntValue("resultCode"), bizPriceSellPriceGetJSONObject.getString("resultMessage"));
        }


        JSONArray jsonArray = bizPriceSellPriceGetJSONObject.getJSONArray("result");
        if ((jsonArray == null) || (jsonArray.size() == 0) || (jsonArray.get(0) == null)) {
            return new ApiResponse(9000, "报价为空！");
        }

        return new QueryResponse(200, "操作成功！", jsonArray);
    }
}