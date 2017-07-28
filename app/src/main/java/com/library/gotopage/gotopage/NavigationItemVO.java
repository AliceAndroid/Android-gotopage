package com.library.gotopage.gotopage;

import com.alibaba.fastjson.JSONObject;

import java.io.Serializable;
import java.util.Map;

/**
 * Activity导航VO
 * Created by lzcheng on 2015/11/6.
 */
public class NavigationItemVO implements Serializable {
    /**
     * activity Class
     */
    public String activityName;
    /**
     * 是否需要登录
     */
    public boolean needLogin;
    /**
     * 是否需要绑卡
     */
    public boolean needBindCard;
    /**
     * 将要传入activity的参数
     */
    public Map<String, String> paraMap;
    /**
     * 打断条件
     */
    public JSONObject breakCondition;
}
