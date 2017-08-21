package com.library.gotopage.manager;

import android.text.TextUtils;
import android.util.Log;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.library.gotopage.base.AuthConfigVO;
import com.library.gotopage.base.BaseConfigVO;
import com.library.gotopage.base.ContextConfigVO;
import com.library.gotopage.base.ResultConfigVO;
import com.library.gotopage.util.Util;

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
            ResultConfigVO resultConfigVO = itemVO.getResult();
            Map<String, String> map = resultConfigVO.getParams();
            resultConfigVO.setParams(Util.getMap(map, params));
            itemVO.setResult(resultConfigVO);
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
            ResultConfigVO configVO = itemVO.getResult();
            configVO.setActivity(className);

            itemVO.setResult(configVO);
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
            boolean exist = true;
            JSONObject condition = conditions.getJSONObject(i);
            for (String key : condition.keySet()) {
                if (!key.equals("result")) {
                    String param = params.getString(key);
                    String conditionParam = condition.getString(key);
                    if (param == null || conditionParam == null || !param.equals(conditionParam)) {
                        exist = false;
                        break;
                    } else {
                        params.remove(key);
                    }
                }
            }
            if (exist) {
                itemVO = getBaseConfigVOByJson(condition);
                break;
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
            ResultConfigVO resultConfigVO = itemVO.getResult();
            resultConfigVO.setActivity(result.getString("activity"));
            JSONObject params = result.getJSONObject("params");
            Map<String, String> map = resultConfigVO.getParams();
            if (null != params) {
                map = Util.getMap(map, params);
            }
            resultConfigVO.setParams(map);

            itemVO.setResult(resultConfigVO);
        }

        JSONArray authGroup = jsonObject.getJSONArray("authGroup");
        if (null != authGroup) {
            List<AuthConfigVO> list = itemVO.getAuthGroup();
            for (int i = 0; i < authGroup.size(); i++) {
                JSONObject object = authGroup.getJSONObject(i);
                AuthConfigVO vo = new AuthConfigVO();
                vo.setAuthClazz(object.getString("authClazz"));
                Map<String, String> params = vo.getParams();
                if (null != object.getJSONObject("params")) {
                    params = Util.getMap(params, object.getJSONObject("params"));
                }
                vo.setParams(params);
                Map<String, String> options = vo.getOptions();
                if (null != object.getJSONObject("options")) {
                    options = Util.getMap(options, object.getJSONObject("options"));
                }
                vo.setOptions(options);

                list.add(vo);
            }

            itemVO.setAuthGroup(list);
        }

        JSONObject context = jsonObject.getJSONObject("context");
        if (null != context) {
            ContextConfigVO contextConfigVO = itemVO.getContext();
            contextConfigVO.setClazz(context.getString("clazz"));
            JSONObject contextParams = context.getJSONObject("options");
            if (null != contextParams) {
                Map<String, String> conMap = contextConfigVO.getOptions();
                conMap = Util.getMap(conMap, contextParams);
                contextConfigVO.setOptions(conMap);
            }

            itemVO.setContext(contextConfigVO);
        }

        return itemVO;
    }
}
