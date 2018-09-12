package com.dianming.jd.interceptor;

import com.dianming.jd.initializer.Logger;
import com.dianming.jd.tool.Fusion;
import com.dianming.jd.tool.RequestException;
import org.springframework.web.servlet.HandlerInterceptor;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

public class ApiInterceptor
        implements HandlerInterceptor {
    private static final Logger log = new Logger("Api拦截器");

    public static void showParams(HttpServletRequest request) {
        log.info(Fusion.getParams(request));
    }

    protected void responseObject(HttpServletResponse response, String ar) {
        response.setCharacterEncoding("UTF-8");
        response.setContentType("application/json; charset=utf-8");
        PrintWriter out = null;
        try {
            out = response.getWriter();
            out.append(ar);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (out != null) {
                out.close();
            }
        }
    }

    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        String uri = request.getServletPath();
        log.info("Api:::::::::::::::::::::::::::" + uri + ":::::::::::::::::::::::::::");
        showParams(request);
        return true;
    }

    public void postHandle(HttpServletRequest request, HttpServletResponse response, Object handler, ModelAndView modelAndView)
            throws Exception {
    }

    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex)
            throws Exception {
        if (ex != null) {
            if ((ex instanceof RequestException)) {
                responseObject(response, ((RequestException) ex).getAr());
            } else {
                responseObject(response, "服务器繁忙！");
            }
        }
    }
}