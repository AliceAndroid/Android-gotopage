package com.library.gotopage.gotopage;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.library.gotopage.util.GotoPageManager;


import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;


/**
 * 内部跳转处理类,首页跳转，webView向APP跳转
 * Created by lzcheng on 2015/9/16.
 * 优化：jmtian
 */
public class NavigationManager {

    private Context navFromeContext;
    private Intent navIntent;
    private boolean navNeedLogin;
    private boolean navNeedBindCard;
    private JSONObject breakCondition;
    private static NavigationManager instance = null;
    private NavigationItemVO itemVO;
    private GotoPageInterface gotoPageInterface;

    public GotoPageInterface getGotoPageInterface() {
        return gotoPageInterface;
    }

    public void setGotoPageInterface(GotoPageInterface gotoPageInterface) {
        this.gotoPageInterface = gotoPageInterface;
    }

    public static NavigationManager getInstance() {
        if (instance == null) {
            instance = new NavigationManager();
        }
        return instance;
    }

    public void gotoPage(String pageID, String params, Activity fromeContext) {
        gotoPage(pageID, params, fromeContext, -1);//传入-1则不设置任何flags
    }

    public void gotoPage(String pageID, Map<String, String> params, Activity fromContext) {
        gotoPage(pageID, params, fromContext, -1);
    }

    public void gotoPage(String pageID, Map<String, String> params, Activity fromContext, int flags) {
        String jsonStr = hashMapToJson(params);
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

    public void gotoPage(String pageID, String params, Activity fromeContext, int flags) {
        navFromeContext = fromeContext;
        JSONObject jsonObject = JSON.parseObject(params);
        NavigationItemVO item = GotoPageManager.getInstance().getNavigeteItemByPageID(pageID, jsonObject, fromeContext);
        if (item == null || TextUtils.isEmpty(item.activityName)) {
            return;
        }
        navigateForAPP(fromeContext, item, flags);

    }


    /**
     * 通过NavigationItemVO处理导航
     *
     * @param fromActivity
     * @param itemVO
     * @param flags        传入-1则intent不设置任何flags
     */
    public void navigateForAPP(Context fromActivity, NavigationItemVO itemVO, int flags) {
        Intent intent = getIntentByNavigation(itemVO, fromActivity);
        if (intent == null) {
            return;
        }
        if (flags != -1) {
            intent.setFlags(flags);
        }
        navigateByIntent(fromActivity, intent, itemVO, itemVO.needLogin, itemVO.needBindCard, itemVO.breakCondition);
    }

    /**
     * 通过Intent跳转
     *
     * @param fromActivity
     * @param intent
     * @param needLogin
     * @param needBindCard
     */
    public void navigateByIntent(Context fromActivity, Intent intent, Boolean needLogin, boolean needBindCard, JSONObject breakCon) {
        NavigationItemVO itemVO = new NavigationItemVO();
        itemVO.needLogin = needLogin;
        itemVO.needBindCard = needBindCard;
        itemVO.breakCondition = breakCon;
        navigateByIntent(fromActivity, intent, itemVO, needLogin, needBindCard, breakCon);

    }

    /**
     * 打断条件完成后调用
     *
     * @param success
     */
    public void navigateForSkipResult(Boolean success) {
        if (success) {
            if (navIntent != null && navFromeContext != null) {
                navigateByIntent(navFromeContext, navIntent, itemVO, navNeedLogin, navNeedBindCard, breakCondition);
            }
        } else {
            navIntent = null;
            navFromeContext = null;
            breakCondition = null;
        }
    }

    /**
     * 获取fromActivity 的全类名
     */
    public String getFromContext() {
        String activityName = null;
        if (navFromeContext != null && navFromeContext instanceof Activity) {
            Activity act = (Activity) navFromeContext;
            activityName = act.getClass().getName();
        }
        return activityName;
    }

    /**
     * 获取NavigationItemVO
     *
     * @param navigationItemVO
     * @param activity
     * @return
     */
    private Intent getIntentByNavigation(NavigationItemVO navigationItemVO, Context activity) {
        Class activityClass = null;
        try {
            activityClass = Class.forName(navigationItemVO.activityName);
        } catch (ClassNotFoundException e) {
            Log.e("NavigationManager", "获取activity失败");
            return null;
        }
        Intent intent = new Intent();
        intent = gotoPageInterface.onGotoPageGetActivityClassListener(activityClass, intent);
        if (navigationItemVO.paraMap != null) {
            for (String key : navigationItemVO.paraMap.keySet()) {
                intent.putExtra(key, navigationItemVO.paraMap.get(key));
            }
        }
        intent.setClass(activity, activityClass);
        return intent;
    }

    /**
     * 通过Intent跳转
     *
     * @param fromActivity
     * @param intent
     * @param needLogin
     * @param needBindCard
     */
    private void navigateByIntent(Context fromActivity, Intent intent, NavigationItemVO navigationItemVO, Boolean needLogin, boolean needBindCard, JSONObject breakCon) {
        itemVO = navigationItemVO;
        navIntent = intent;
        navNeedLogin = needLogin;
        navNeedBindCard = needBindCard;
        navFromeContext = fromActivity;
        breakCondition = breakCon;
        if (gotoPageInterface.onGotoPageConditionBackListener(itemVO, fromActivity)) {
            if (isBreakSkip(breakCon)) {
                // 判断是否打断
                navIntent = null;
                navFromeContext = null;
                breakCondition = null;
            } else {
                handleNavigetion();
            }
        }

    }

    /**
     * 验证完毕，直接跳转
     */
    private void handleNavigetion() {
        navFromeContext.startActivity(navIntent);
        navIntent = null;
        navFromeContext = null;
    }

    /**
     * 判断是否需要打断跳转
     */
    private boolean isBreakSkip(JSONObject breakCondition) {
        boolean interrupt = false;
        if (breakCondition != null) {
            int matched = 0;
            for (String key : breakCondition.keySet()) {
                String value = breakCondition.getString(key);
                String userVOValue = gotoPageInterface.onGotoPageGetUserPropertyByName(key);

                if (value != null && userVOValue.matches(value)) {
                    matched++;
                }
            }
            if (matched == breakCondition.size()) {
                interrupt = true;
            }
        }
        return interrupt;
    }

    private String hashMapToJson(Map map) {
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

}
