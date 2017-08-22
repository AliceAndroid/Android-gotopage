package com.library.gotopage.manager;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.library.gotopage.base.AuthConfigVO;
import com.library.gotopage.base.BaseConfigVO;
import com.library.gotopage.util.Util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;


/**
 * Author:jmtian
 * Commemt:gotopagec初始化类
 * Date: 2017/7/24 14:53
 */
public class RouterManager {
    private String TAG = "RouterManager";
    private JSONArray pageJsonArray;

    private static final RouterManager instance = new RouterManager();

    private RouterManager() {
    }

    public static RouterManager getInstance() {
        return instance;
    }

    public void init(JSONArray pageJsonArray) {
        this.pageJsonArray = pageJsonArray;
    }

    /**
     * 根据pageID获取BaseConfigVO
     *
     * @param pageID
     * @param params
     * @return
     */
    public BaseConfigVO getNavigationItemByPageID(String pageID, JSONObject params) {
        if (TextUtils.isEmpty(pageID)) {
            return null;
        }
        BaseConfigVO itemVO = null;
        for (int i = 0; i < pageJsonArray.size(); i++) {
            JSONObject item = pageJsonArray.getJSONObject(i);
            if (pageID.equals(item.getString("pageId"))) {
                JSONArray conditions = item.getJSONArray("conditions");
                if (conditions != null && conditions.size() > 0) {
                    itemVO = getBaseConfigVOFromConditions(conditions, params);
                } else {
                    itemVO = getBaseConfigVOByJson(item);
                }
                break;
            }
        }
        if (null == itemVO) {
            itemVO = getNavigationItemByClassName(pageID);
        }
        if (null != itemVO && null != params) {
            Map<String, String> map = itemVO.getParams();
            itemVO.setParams(Util.getMap(map, params));
        }
        return itemVO;
    }

    /**
     * 根据activityName 获取pageID
     *
     * @param className
     * @return
     */
    public String getPageIdByClassName(String className) {
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

    private BaseConfigVO getNavigationItemByClassName(String className) {
        Class activityClass = null;
        BaseConfigVO itemVO = null;
        try {
            activityClass = Class.forName(className);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "获取Activity失败");
            return null;
        } catch (Exception e) {

        }
        if (null != activityClass) {
            itemVO = new BaseConfigVO();
            itemVO.setActivity(className);
        }
        return itemVO;
    }

    /**
     * 从conditions里匹配BaseConfigVO
     *
     * @param conditions
     * @param params
     * @return
     */
    private BaseConfigVO getBaseConfigVOFromConditions(JSONArray conditions, JSONObject params) {
        BaseConfigVO itemVO = null;
        for (int i = 0; i < conditions.size(); i++) {
            JSONObject condition = conditions.getJSONObject(i);
            if (condition.containsKey("type")) {
                String param = params.getString("type");
                String conditionParam = condition.getString("type");
                if (null != param && null != conditionParam && param.equals(conditionParam)) {
                    itemVO = getBaseConfigVOByJson(condition);
                    break;
                }
            } else {
                itemVO = getBaseConfigVOByJson(condition);
            }

        }
        return itemVO;
    }

    /**
     * 将result,context的jsonObject 转变为 BaseConfigVO
     *
     * @param jsonObject
     * @return
     */
    private BaseConfigVO getBaseConfigVOByJson(JSONObject jsonObject) {
        BaseConfigVO itemVO = new BaseConfigVO();

        JSONObject result = jsonObject.getJSONObject("result");
        if (null != result) {
            itemVO.setActivity(result.getString("activity"));
            JSONObject params = result.getJSONObject("params");
            Map<String, String> map = itemVO.getParams();
            if (null != params) {
                map = Util.getMap(map, params);
                itemVO.setParams(map);
            }
        }

        JSONArray authGroup = jsonObject.getJSONArray("authGroup");
        if (null != authGroup) {
            List<AuthConfigVO> list = new ArrayList<>();
            for (int i = 0; i < authGroup.size(); i++) {
                JSONObject object = authGroup.getJSONObject(i);
                AuthConfigVO vo = new AuthConfigVO();
                vo.setAuthClazz(object.getString("authClazz"));
                Map<String, String> params = vo.getParams();
                if (null != object.getJSONObject("params")) {
                    params = Util.getMap(params, object.getJSONObject("params"));
                    vo.setParams(params);
                }

                Map<String, String> options = vo.getOptions();
                if (null != object.getJSONObject("options")) {
                    options = Util.getMap(options, object.getJSONObject("options"));
                    vo.setOptions(options);
                }
                list.add(vo);
            }

            itemVO.setAuthGroup(list);
        }

        JSONObject context = jsonObject.getJSONObject("context");
        if (null != context) {
            itemVO.setClazz(context.getString("clazz"));
            JSONObject contextParams = context.getJSONObject("options");
            if (null != contextParams) {
                Map<String, String> conMap = itemVO.getOptions();
                conMap = Util.getMap(conMap, contextParams);
                itemVO.setOptions(conMap);
            }
        }

        return itemVO;
    }
}
