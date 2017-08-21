package com.library.gotopage.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:jmtian
 * Date: 2017/8/14 17:06
 * description: 授权组对应验证条件itemVO
 */


public class AuthConfigVO implements Serializable {
    /**
     * 验证条件传入参数
     */
    private Map<String, String> params = new HashMap<>();
    /**
     * 验证条件配置项
     */
    private Map<String, String> options = new HashMap<>();
    /**
     * 验证类
     */
    private String authClazz;

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public String getAuthClazz() {
        return authClazz;
    }

    public void setAuthClazz(String authClazz) {
        this.authClazz = authClazz;
    }

    public Map<String, String> getOptions() {
        return options;
    }

    public void setOptions(Map<String, String> options) {
        this.options = options;
    }

    /**
     * 获取验证类的类名
     *
     * @return
     */
    public String getAuthClazzName() {
        return authClazz.substring(authClazz.lastIndexOf("\\."));
    }
}
