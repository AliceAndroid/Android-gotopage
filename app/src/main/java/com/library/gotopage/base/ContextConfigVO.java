package com.library.gotopage.base;

import java.util.HashMap;
import java.util.Map;

/**
 * Name:ContextConfigVO
 * Author:jmtian
 * Commemt:baseContext子类信息VO
 * Date: 2017/8/21 16:41
 */

public class ContextConfigVO {
    /**
     * baseContext 子类
     */
    private String clazz;
    /**
     * baseContext 子类需要的配置项
     */
    private Map<String, String> options = new HashMap<>();

    public String getClazz() {
        return clazz;
    }

    public void setClazz(String clazz) {
        this.clazz = clazz;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }
}
