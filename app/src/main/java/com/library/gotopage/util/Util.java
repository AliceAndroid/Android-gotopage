package com.library.gotopage.util;

import com.alibaba.fastjson.JSONObject;

import java.util.Iterator;
import java.util.Map;

/**
 * Author:jmtian
 * Commemt:工具类
 * Date: 2017/8/16 15:09
 */


public class Util {
    /**
     * map转换为json
     *
     * @param map
     * @return
     */
    public static String hashMapToJson(Map map) {
        if (map == null || map.isEmpty() || map.size() == 0) {
            return "{}";
        }
        String string = "{";
        for (Iterator it = map.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry e = (Map.Entry) it.next();
            string += "'" + e.getKey() + "':";
            string += "'" + e.getValue() + "',";
        }
        string = string.substring(0, string.lastIndexOf(","));
        string += "}";
        return string;
    }

    /**
     * 遍历JSONObject数据到map集合
     *
     * @param map
     * @param params
     * @return
     */
    public static Map<String, String> getMap(Map<String, String> map, JSONObject params) {
        for (String key : params.keySet()) {
            map.put(key, params.getString(key));
        }
        return map;
    }
}
