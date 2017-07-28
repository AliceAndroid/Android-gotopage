package com.library.gotopage.util;

import android.content.Context;
import android.content.res.AssetManager;
import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.library.gotopage.gotopage.GotoPageInterface;
import com.library.gotopage.gotopage.NavigationItemVO;
import com.library.gotopage.gotopage.NavigationManager;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;


/**
 * Name:GotoPageManager
 * Author:jmtian
 * Commemt:gotopage管理类
 * Date: 2017/7/24 14:53
 */
public class GotoPageManager {
    private GotoPageInterface gotoInterface;
    private JSONArray pageJsonArray;
    private String pathFile;//本地问价在assets的路径

    private static final GotoPageManager instance = new GotoPageManager();

    private GotoPageManager() {
    }

    public static GotoPageManager getInstance() {
        return instance;
    }

    /**
     * Author:jmtian
     * Date: 2017/7/24 16:05
     * description:初始化
     *
     * @param gotoInterface
     * @param pageJsonArray
     * @param pathFile      本地json文件在assets的路径，例如：config/activityConfig.json
     */

    public void init(Context context, GotoPageInterface gotoInterface, JSONArray pageJsonArray, String pathFile) {
        this.gotoInterface = gotoInterface;
        this.pageJsonArray = pageJsonArray;
        this.pathFile = pathFile;
        if (null == pageJsonArray) {
            parsePageConfig(context);
        } else {
            NavigationManager.getInstance().setGotoPageInterface(gotoInterface);
        }

    }

    /**
     * 根据pageID获取NavigationItemVO
     *
     * @param pageID
     * @param params
     * @param context
     * @return
     */
    public NavigationItemVO getNavigeteItemByPageID(String pageID, JSONObject params, Context context) {
        if (pageJsonArray == null) {
            parsePageConfig(context);
        }
        if (TextUtils.isEmpty(pageID)) {
            return null;
        }
        NavigationItemVO itemVO = null;
        for (int i = 0; i < pageJsonArray.size(); i++) {
            JSONObject item = pageJsonArray.getJSONObject(i);
            if (pageID.equals(item.getString("pageId"))) {
                JSONArray conditions = item.getJSONArray("conditions");
                if (conditions != null && conditions.size() > 0) {
                    itemVO = getNavigationItemFromConditions(conditions, params);
                } else {
                    itemVO = getNavigationItemByJson(item.getJSONObject("result"));
                }
                break;
            }
        }
        if (null == itemVO) {
            itemVO = getNavigationItemByClassName(pageID);
        }
        if (itemVO != null && null != params) {
            for (String key : params.keySet()) {
                if (key.equals("needLogin")) {
                    itemVO.needLogin = Boolean.valueOf(params.getString(key));
                } else if (key.equals("needBindCard")) {
                    itemVO.needBindCard = Boolean.valueOf(params.getString(key));
                } else if (key.equals("break")) {
                    itemVO.breakCondition = params.getJSONObject(key);
                } else {
                    itemVO.paraMap.put(key, params.getString(key));
                }
            }
        }
        return itemVO;
    }

    /**
     * 根据activityName 获取pageID
     *
     * @param className
     * @param context
     * @return
     */
    public String getPageIdByClassName(String className, Context context) {
        if (pageJsonArray == null) {
            parsePageConfig(context);
        }
        String pageId = null;
        for (int i = 0; i < pageJsonArray.size(); i++) {
            JSONObject item = pageJsonArray.getJSONObject(i);
            JSONArray conditions = item.getJSONArray("conditions");
            if (conditions != null && conditions.size() > 0) {
                for (int j = 0; j < conditions.size(); j++) {
                    JSONObject object = conditions.getJSONObject(j);
                    JSONObject result = object.getJSONObject("result");
                    if (result.get("activity").equals(className)) {
                        pageId = item.getString("pageId");
                        break;
                    }
                }
            } else {
                JSONObject result = item.getJSONObject("result");
                if (result.get("activity").equals(className)) {
                    pageId = item.getString("pageId");
                    break;
                }
            }
        }
        return pageId;
    }

    private NavigationItemVO getNavigationItemByClassName(String className) {
        Class activityClass = null;
        NavigationItemVO itemVO = null;
        try {
            activityClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.e("NavigationManager", "获取activity失败");
            return null;
        }
        if (null != activityClass) {
            itemVO = new NavigationItemVO();
            itemVO.activityName = className;
        }
        return itemVO;
    }

    /**
     * 从conditions里匹配NavigationItemVO
     *
     * @param conditions
     * @param params
     * @return
     */
    private NavigationItemVO getNavigationItemFromConditions(JSONArray conditions, JSONObject params) {
        NavigationItemVO itemVO = null;
        for (int i = 0; i < conditions.size(); i++) {
            boolean exist = true;
            JSONObject condition = conditions.getJSONObject(i);
            for (String key : condition.keySet()) {
                if (!key.equals("result")) {
                    String param = params.getString(key);
                    String conditionParm = condition.getString(key);
                    if (param == null || conditionParm == null || !param.equals(conditionParm)) {
                        exist = false;
                        break;
                    } else {
                        params.remove(key);
                    }
                }
            }
            if (exist) {
                itemVO = getNavigationItemByJson(condition.getJSONObject("result"));
                break;
            }
        }
        return itemVO;
    }

    /**
     * 将result的jsonObject 转变为 NavigationItemVO
     *
     * @param jsonObject
     * @return
     */
    private NavigationItemVO getNavigationItemByJson(JSONObject jsonObject) {
        NavigationItemVO itemVO = new NavigationItemVO();
        itemVO.activityName = jsonObject.getString("activity");
        itemVO.needLogin = jsonObject.getBooleanValue("isLogin");
        itemVO.needBindCard = jsonObject.getBooleanValue("isBindCard");
        JSONObject params = jsonObject.getJSONObject("params");
        itemVO.paraMap = new HashMap<String, String>();
        if (params != null) {
            itemVO.paraMap = new HashMap<String, String>();
            for (String key : params.keySet()) {
                itemVO.paraMap.put(key, params.getString(key));
            }
        }
        return itemVO;
    }

    /**
     * 解析文件内容到JSONArray，缓存起来
     *
     * @param context
     */
    private void parsePageConfig(Context context) {
        StringBuilder stringBuilder = new StringBuilder();
        try {
            AssetManager assetManager = context.getAssets();
            BufferedReader bf = new BufferedReader(new InputStreamReader(
                    assetManager.open(pathFile)));
            String line;
            while ((line = bf.readLine()) != null) {
                stringBuilder.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        JSONObject jsonObject = JSONObject.parseObject(stringBuilder.toString());
        pageJsonArray = jsonObject.getJSONArray("pages");
        NavigationManager.getInstance().setGotoPageInterface(gotoInterface);
    }
}
