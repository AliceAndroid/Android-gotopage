package com.library.gotopage.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Name:ResultConfigVO
 * Author:jmtian
 * Commemt:result 相关VO
 * Date: 2017/8/21 16:42
 */


public class ResultConfigVO {
    /**
     * activity Class
     */
    private String activity;
    /**
     * 将要传入activity的参数
     */
    private Map<String,String> params = new HashMap<>();

    public String getActivity() {
        return activity;
    }

    public void setActivity(String activity) {
        this.activity = activity;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }
}
