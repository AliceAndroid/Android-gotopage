package com.library.gotopage.manager;

import android.app.Activity;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.library.gotopage.base.BaseConfigVO;
import com.library.gotopage.base.BaseContext;
import com.library.gotopage.base.BasePreAuthCondition;
import com.library.gotopage.util.Util;


import java.util.HashMap;
import java.util.Map;


/**
 * Name:NavigationManager
 * Author:jmtian
 * Commemt:路由跳转调用类
 * Date: 2017/8/16 11:24
 */

public class NavigationManager {
    private static final String TAG = "NavigationManager";

    private Context fromContext;
    private BaseContext baseContext;

    private static NavigationManager instance = null;

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void gotoPage(String pageID, String params, Activity fromContext) {
        gotoPage(pageID, params, fromContext, -1);//传入-1则不设置任何flags
    }

    public void gotoPage(String pageID, Map<String, String> params, Activity fromContext) {
        gotoPage(pageID, params, fromContext, -1);
    }

    public void gotoPage(String pageID, Map<String, String> params, Activity fromContext, int flags) {
        String jsonStr = Util.hashMapToJson(params);
        gotoPage(pageID, jsonStr, fromContext, flags);
    }

    public void gotoPage(String pageID, String paramKey, String paramUrl, Activity fromContext) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(paramKey, paramUrl);
        gotoPage(pageID, map, fromContext, -1);
    }

    public void gotoPage(String pageID, String paramKey, String paramUrl, Activity fromContext, int flags) {
        Map<String, String> map = new HashMap<String, String>();
        map.put(paramKey, paramUrl);
        gotoPage(pageID, map, fromContext, flags);
    }

    public void gotoPage(String params, Activity fromContext, int flags) {
        if (TextUtils.isEmpty(params)) {
            return;
        }
        try {
            JSONObject jsonObject = JSON.parseObject(params);
            String pageID = jsonObject.getString("pageId");
            gotoPage(pageID, params, fromContext, flags);
        } catch (Exception exception) {
            Log.e("NavigationManager", "navigateItemJson was parsed failed");
        }
    }

    public void gotoPage(String params, Activity fromContext) {
        gotoPage(params, fromContext, -1);
    }

    public void gotoPage(String pageID, String params, Activity fromContext, int flags) {
        this.fromContext = fromContext;
        JSONObject jsonObject = JSON.parseObject(params);
        BaseConfigVO config = RouterManager.getInstance().getNavigationItemByPageID(pageID, jsonObject);
        if (null == config || TextUtils.isEmpty(config.getActivity())) {
            return;
        }
        navigateForAPP(fromContext, config, flags);

    }


    /**
     * 通过BaseConfigVO里面的contextClass 创建不同的baseContext
     *
     * @param fromActivity
     * @param config
     * @param flags
     */
    public void navigateForAPP(Activity fromActivity, BaseConfigVO config, int flags) {
        // 根据config获取target
        if (!TextUtils.isEmpty(config.getClazz())) {
            baseContext = getBaseContext(fromActivity, config, flags);
        } else {
            baseContext = new BaseContext(fromActivity, config, flags);
        }
        navigateTo();
    }

    /**
     * 获取BaseContext子类
     *
     * @param fromActivity
     * @param config
     * @param flags
     * @return
     */
    private BaseContext getBaseContext(Activity fromActivity, BaseConfigVO config, int flags) {
        try {
            Class<BaseContext> clazz = (Class<BaseContext>) Class.forName(config.getClazz());
            baseContext = clazz.getConstructor(Activity.class, BaseConfigVO.class, int.class).newInstance(fromActivity, config, flags);
        } catch (Exception e) {
            baseContext = new BaseContext(fromActivity, config, flags);
        }
        return baseContext;
    }

    /**
     * 条件验证，页面跳转
     */
    private void navigateTo() {
        if (null == baseContext) {
            return;
        }
        BasePreAuthCondition currentCondition = baseContext.getCurrentCondition();
        if (currentCondition != null) {
            // 如果有上一个用户刚完成操作的前置条件，则需要执行前置条件的after回调
            currentCondition.after();
        } else if (baseContext.hasUnVerifyCondition()) {
            // 如果有未完成验证的前置条件，需要继续验证
            baseContext.next();
        } else {
            // 以上都不符合，直接跳转目标页面
            baseContext.gotoTarget();
        }
    }

    /**
     * 打断条件完成后调用
     */
    public void navigateForSkipResult() {
        if (null == baseContext) {
            return;
        }
        navigateTo();
    }

    /**
     * 获取fromActivity 的全类名
     */
    public String getFromContext() {
        String activityName = null;
        if (fromContext != null && fromContext instanceof Activity) {
            Activity act = (Activity) fromContext;
            activityName = act.getClass().getName();
        }
        return activityName;
    }

    /**
     * 是否资源
     */
    public void release() {
        fromContext = null;
        baseContext = null;
    }
}
