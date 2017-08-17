package com.library.gotopage.base;

import java.io.Serializable;
import java.util.HashMap;
import java.util.Map;

/**
 * Author:jmtian
 * Date: 2017/8/14 17:06
 * description: 授权组对应验证条件itemVO
 */


public class AuthConfigVO implements Serializable{
    private Map<String, String> params = new HashMap<>();//验证条件类配置信息 如绑卡成功后到目标页面还是回到原页面

    private String authClazz;//验证类

    private String authClazzName;//验证类类名

    public String getAuthClazzName() {
        return authClazz.substring(authClazz.lastIndexOf("\\."));
    }

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
}
