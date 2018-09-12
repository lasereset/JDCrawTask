package com.dianming.jd.tool;

import java.io.File;
import java.net.HttpURLConnection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;


public class HttpRequestText {

    // 本地处理失败
    public static final int HANDLE_EXCEPTION = 1000;
    // 网络异常导致失败
    public static final int NETWORK_EXCEPTION = 1001;
    // 返回内容非法
    public static final int RESPONSE_EXCEPTION = 1002;
    // 没有定义方法
    public static final int RESPONSE_NO_TASK = 1003;
    // 没有定义方法
    public static final int RESPONSE_USER_CANCEL = 1004;
    // 连接超时
    public static final int REQUEST_TIMTOUT = 15000;// 10 sec
    // 读超时
    public static final int READ_TIMTOUT = 15000;// 15 sec
    
    public static final int OK = HttpURLConnection.HTTP_OK;
    public static final int FAILED = -1;

    public static interface IAsyncPostTask {
        // 由父类继续处理 播报结果
        public static final boolean CONTINUE = false;
        // 处理完毕
        public static final boolean FINSHED = true;

        // 独立线程
        // 返回错误码
        public int handleResponse(String response) throws Exception;

        // 主线程
        // 返回是否需要额外处理(已经自己播报默认成功语音)
        public boolean onSuccess();

        // 主线程
        // 返回是否需要额外处理(已经自己播报默认失败语音)
        public boolean onFail();

    }

    public static class DefaultAsyncPostTask implements IAsyncPostTask {

        @Override
        public int handleResponse(String response) throws Exception {
            return OK;
        }

        @Override
        public boolean onSuccess() {
            return CONTINUE;
        }

        @Override
        public boolean onFail() {
            return CONTINUE;
        }

    }

    @SuppressWarnings("unused")
    private IAsyncPostTask task;
    private boolean prompt = true;
    final Map<String, String> headers = new HashMap<String, String>();

    public void setHeader(String field, String value) {
        headers.put(field, value);
    }

    String getPostError(int errCode) {
        switch (errCode) {
            case HANDLE_EXCEPTION: {
                return "和服务器通讯时发生异常";
            }
            case NETWORK_EXCEPTION: {
                return "无法连接服务器,或者服务器返回超时";
            }
            case RESPONSE_EXCEPTION: {
                return "服务器返回数据格式错误";
            }
            case RESPONSE_USER_CANCEL: {
                return "用户取消";
            }
            default:
            case RESPONSE_NO_TASK: {
                return "网络请求失败";
            }
        }
    }

    public void request(String url, IAsyncPostTask task) {
        this.task = task;
        
        try {
            HttpRequest request = HttpRequest.post(url);
            request.connectTimeout(REQUEST_TIMTOUT);
            request.readTimeout(REQUEST_TIMTOUT);
            request.form(headers);
            int retCode = doRequst(request);
            if (retCode == OK) {
                String response = request.body();
                if (response != null) {
                    // System.out.println("[网络]BODY:" + response);
                    task.handleResponse(response);                    
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }        
    }

    public String request(String url) {
        try {
            // System.out.println("post url:" + url);
            HttpRequest request = HttpRequest.post(url);
            request.connectTimeout(REQUEST_TIMTOUT);
            request.readTimeout(REQUEST_TIMTOUT);
            request.form(headers);
            int retCode = doRequst(request);
            String response = request.body();
             if (retCode == 500 || retCode == 404) {
            	System.out.println("retCode:" + retCode + ", body:" + response);
             }
            if (retCode == OK) {
                return response;
            }
        } catch (Exception e) {
        	e.printStackTrace();
        }        
        return null;
    }
    
    public String upload(String url, File file) {
    	try {
    		HttpRequest request = HttpRequest.post(url);
            request.connectTimeout(REQUEST_TIMTOUT);
            request.readTimeout(3 * READ_TIMTOUT);
            
            Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
            while (iterator.hasNext()) {
                Entry<String, String> entry = iterator.next();
                String key = entry.getKey();
                String values = entry.getValue();
                request.part(key, values);
            }
            
            request.part("file", file.getName(), file);
            int retCode = doRequst(request);
            String response = request.body();
            if (retCode == 500 || retCode == 404) {
                System.out.println("retCode:" + retCode + ", body:" + response);
            }
            if (retCode == OK) {
                return response;
            }            
    	} catch (Exception e) {
    		e.printStackTrace();
    	}
    	return null;
    }
    
    protected int doRequst(HttpRequest request) throws HttpRequest.HttpRequestException {
        printHeaders(headers);
        if (!request.ok()) {
            return request.code();
        }
        // printResponseHeader(request);
        return OK;

    }

    @SuppressWarnings("unused")
    void printHeaders(Map<String, String> headers) {
        Iterator<Entry<String, String>> iterator = headers.entrySet().iterator();
        while (iterator.hasNext()) {
            Entry<String, String> entry = iterator.next();
            String key = entry.getKey();
            String values = entry.getValue();
            // System.out.println("[网络]HEAD  " + key + ":" + values);
        }
    }

    @SuppressWarnings("unused")
    void printResponseHeader(HttpRequest request) {
        Map<String, List<String>> resHeaders = request.headers();
        Iterator<Entry<String, List<String>>> iterator = resHeaders.entrySet().iterator();

        while (iterator.hasNext()) {
            Entry<String, List<String>> entry = iterator.next();
            String key = entry.getKey();
            List<String> values = entry.getValue();
            for (String string : values) {
                // System.out.println("[网络]HEAD  " + key + ":" + string);
            }
        }
    }

    public boolean isPrompt() {
        return prompt;
    }

    public void setPrompt(boolean prompt) {
        this.prompt = prompt;
    }
}
