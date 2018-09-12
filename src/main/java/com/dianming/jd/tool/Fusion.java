package com.dianming.jd.tool;

import com.alibaba.fastjson.JSON;

import javax.servlet.http.HttpServletRequest;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.URLEncoder;
import java.util.*;
import java.util.Map.Entry;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.GZIPInputStream;

public class Fusion {
    public static boolean isEmpty(Object object) {
        if (object == null) {
            return true;
        }
        if ((object instanceof String))
            return ((String) object).length() == 0;
        if ((object instanceof List))
            return ((List) object).isEmpty();
        if ((object instanceof Map))
            return ((Map) object).isEmpty();
        if ((object instanceof Collection)) {
            return ((Collection) object).isEmpty();
        }
        return false;
    }


    public static boolean isEmpty(String... strings) {
        for (String str : strings) {
            if (isEmpty(str)) {
                return true;
            }
        }
        return false;
    }


    public static String uncompressToString(byte[] b) {
        if ((b == null) || (b.length == 0)) {
            return null;
        }
        ByteArrayOutputStream out = new ByteArrayOutputStream();
        ByteArrayInputStream in = new ByteArrayInputStream(b);
        try {
            GZIPInputStream gunzip = new GZIPInputStream(in);
            byte[] buffer = new byte['Ā'];
            int n;
            while ((n = gunzip.read(buffer)) >= 0) {
                out.write(buffer, 0, n);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return out.toString();
    }


    public static String getAbstract(String content, int length) {
        if (!isEmpty(content)) {
            int subEnd = length;
            if (subEnd >= content.length()) {
                return content;
            }
            return content.substring(0, subEnd) + "...";
        }
        return "";
    }


    public static <T> T getObject(String ts, Class<T> tc) {
        if (isEmpty(ts)) {
            return null;
        }
        try {
            return (T) JSON.parseObject(ts, tc);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }


    public static <T> List<T> getList(String ts, Class<T> tc) {
        if (isEmpty(ts)) {
            return null;
        }
        try {
            return JSON.parseArray(ts, tc);
        } catch (Exception e) {
        }


        return null;
    }


    public static String listToString(List<?> list) {
        StringBuilder sb = new StringBuilder();
        if ((list != null) && (list.size() > 0)) {
            for (int i = 0; i < list.size(); i++) {
                if (i < list.size() - 1) {
                    sb.append(list.get(i) + ",");
                } else {
                    sb.append(list.get(i));
                }
            }
        }
        return sb.toString();
    }


    public static String getParams(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();

        sb.append(request.getRequestURI() + "\n");
        sb.append("------------------------------\n");
        sb.append(getOnlyParams(request));
        sb.append("------------------------------\n");

        return sb.toString();
    }


    public static String getOnlyParams(HttpServletRequest request) {
        StringBuilder sb = new StringBuilder();
        HashMap map = new HashMap();
        Enumeration paramNames = request.getParameterNames();
        while (paramNames.hasMoreElements()) {
            String paramName = (String) paramNames.nextElement();
            String[] paramValues = request.getParameterValues(paramName);
            if (paramValues.length == 1) {
                String paramValue = paramValues[0];
                map.put(paramName, paramValue);
            } else {
                String paramValue = paramValues[0];
                map.put(paramName, paramValue);
            }
        }
        Set<Entry<String, String>> set = map.entrySet();
        for (Entry entry : set) {
            sb.append(entry.getKey() + ":" + entry.getValue() + "\n");
        }
        return sb.toString();
    }


    public static String formatContent(String content) {
        return content.replaceAll("<", "&lt;").replaceAll(">", "&gt;");
    }


    public static String getMatchString(String sw) {
        return "%" + sw + "%";
    }


    public static <T extends Enum<T>> T valueOf(Class<T> clazz, String text) {
        try {
            return Enum.valueOf(clazz, text);
        } catch (Exception e) {
        }
        return null;
    }


    public static final String getIpAddress(HttpServletRequest request) {
        String ip = request.getHeader("X-Forwarded-For");
        try {
            if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
                if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
                    ip = request.getHeader("Proxy-Client-IP");
                }

                if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
                    ip = request.getHeader("WL-Proxy-Client-IP");
                }

                if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
                    ip = request.getHeader("HTTP_CLIENT_IP");
                }

                if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
                    ip = request.getHeader("HTTP_X_FORWARDED_FOR");
                }

                if ((ip == null) || (ip.length() == 0) || ("unknown".equalsIgnoreCase(ip))) {
                    ip = request.getRemoteAddr();
                }
            } else if (ip.length() > 15) {
                String[] ips = ip.split(",");
                for (int index = 0; index < ips.length; index++) {
                    String strIp = ips[index];
                    if (!"unknown".equalsIgnoreCase(strIp)) {
                        ip = strIp;
                        break;
                    }
                }
            }
        } catch (Exception e) {
        }


        return ip;
    }


    public static double distance(double long1, double lat1, double long2, double lat2) {
        double R = 6378137.0D;
        lat1 = lat1 * 3.141592653589793D / 180.0D;
        lat2 = lat2 * 3.141592653589793D / 180.0D;
        double a = lat1 - lat2;
        double b = (long1 - long2) * 3.141592653589793D / 180.0D;


        double sa2 = Math.sin(a / 2.0D);
        double sb2 = Math.sin(b / 2.0D);
        double d = 2.0D * R * Math.asin(Math.sqrt(sa2 * sa2 + Math.cos(lat1) * Math.cos(lat2) * sb2 * sb2));
        return d;
    }


    public static boolean isLetter(String value) {
        String regex = ".*[a-zA-Z]+.*";
        Matcher matcher = Pattern.compile(regex).matcher(value);
        return matcher.matches();
    }


    public static String removeBlank(String content) {
        if (org.springframework.util.StringUtils.isEmpty(content)) {
            return null;
        }
        char[] value = content.trim().toCharArray();

        String result = "";

        for (int i = 0; i < value.length; i++) {
            if (!Character.isSpaceChar(value[i])) {
                result = result + value[i];
            }
        }
        return result;
    }


    public static String formatUrlMap(Map<String, String> paraMap, boolean urlEncode, boolean keyToLower) {
        String buff = "";
        Map<String, String> tmpMap = paraMap;
        try {
            List<Entry<String, String>> infoIds = new ArrayList(tmpMap.entrySet());

            // 对所有传入参数按照字段名的 ASCII 码从小到大排序（字典序）
            Collections.sort(infoIds, new Comparator<Entry<String, String>>() {

                @Override
                public int compare(Map.Entry<String, String> o1, Map.Entry<String, String> o2) {
                    return (o1.getKey()).toString().compareTo(o2.getKey());
                }
            });

            StringBuilder buf = new StringBuilder();
            for (Entry<String, String> item : infoIds) {
                if (org.apache.commons.lang3.StringUtils.isNotBlank(item.getKey())) {
                    String key = (String) item.getKey();
                    String val = (String) item.getValue();
                    if (urlEncode) {
                        val = URLEncoder.encode(val, "utf-8");
                    }
                    if (keyToLower) {
                        buf.append(key.toLowerCase() + "=" + val);
                    } else {
                        buf.append(key + "=" + val);
                    }
                    buf.append("&");
                }
            }

            buff = buf.toString();
            if (!buff.isEmpty()) {
                buff = buff.substring(0, buff.length() - 1);
            }
        } catch (Exception e) {
            return null;
        }
        return buff;
    }


    public static double calculate(double value1, double value2, int type) {
        switch (type) {
            case 0:
                return new BigDecimal(value1 + value2).setScale(2, RoundingMode.CEILING).doubleValue();
            case 1:
                return new BigDecimal(value1 - value2).setScale(2, RoundingMode.CEILING).doubleValue();
            case 2:
                return new BigDecimal(value1 * value2).setScale(2, RoundingMode.CEILING).doubleValue();
            case 3:
                return new BigDecimal(value1 / value2).setScale(2, RoundingMode.CEILING).doubleValue();
        }
        return 0.0D;
    }
}
