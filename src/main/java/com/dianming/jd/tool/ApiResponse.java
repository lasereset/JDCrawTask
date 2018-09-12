package com.dianming.jd.tool;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonInclude.Include;
import com.fasterxml.jackson.annotation.JsonView;


@JsonInclude(Include.NON_NULL)
public class ApiResponse {
    @JsonView({Object.class})
    String result;
    @JsonView({Object.class})
    int code;

    public ApiResponse() {
    }

    public ApiResponse(int code, String result) {
        this.result = result;
        this.code = code;
        toString();
    }

    public ApiResponse(int code) {
        this.code = code;
    }

    public String getResult() {
        return this.result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public int getCode() {
        return this.code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String toString() {
        return "ApiResponse [result=" + this.result + ", code=" + this.code + "]";
    }

    public static String getErrorPlainText(int code, String result) {
        ApiResponse ar = new ApiResponse(code, result);
        return JSON.toJSONString(ar);
    }
}