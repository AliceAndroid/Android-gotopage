package com.library.gotopage.base;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Name:BaseConfigVO
 * Author:jmtian
 * Commemt:配置文件对应的 一个跳转
 * Date: 2017/8/14 16:59
 */

public class BaseConfigVO implements Serializable {
    private ResultConfigVO result = new ResultConfigVO();//目标类
    private List<AuthConfigVO> authGroup = new ArrayList<>();//权限组
    private ContextConfigVO context = new ContextConfigVO();//baseContext 子类相关信息

    public ResultConfigVO getResult() {
        return result;
    }

    public void setResult(ResultConfigVO result) {
        this.result = result;
    }

    public List<AuthConfigVO> getAuthGroup() {
        return authGroup;
    }

    public void setAuthGroup(List<AuthConfigVO> authGroup) {
        this.authGroup = authGroup;
    }

    public ContextConfigVO getContext() {
        return context;
    }

    public void setContext(ContextConfigVO context) {
        this.context = context;
    }

    /**
     * 根据验证对象获取对应下标
     *
     * @param auth
     * @return
     */
    public int getAuthConditionIndex(BasePreAuthCondition auth) {
        if (null == authGroup || authGroup.isEmpty()) {
            return -1;
        }
        for (int i = 0; i < authGroup.size(); i++) {
            AuthConfigVO vo = authGroup.get(i);
            if ((vo.getAuthClazzName()).equals(auth.getClass().getSimpleName())) {
                return i;
            }
        }
        return -1;
    }

    /**
     * 根据下面获取验证类
     *
     * @param index
     * @return
     */
    public BasePreAuthCondition getAuthConditionByIndex(BaseContext context, int index) {
        if (null == authGroup || authGroup.isEmpty()) {
            return null;
        }
        if (index < authGroup.size()) {
            try {
                Class<BasePreAuthCondition> clazz = (Class<BasePreAuthCondition>) Class.forName(authGroup.get(index).getAuthClazz());
                BasePreAuthCondition vo = clazz.getConstructor(BaseContext.class).newInstance(context);
                return vo;
            } catch (InstantiationException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return null;
    }

    /**
     * 获取权限组的数量
     *
     * @return
     */
    public int getAuthConditionSize() {
        return authGroup.size();
    }

    public Map<String, String> getAuthConditionParams(int index) {
        if (null == authGroup || authGroup.isEmpty()) {
            return null;
        }
        if (index < authGroup.size()) {
            return authGroup.get(index).getParams();
        }
        return null;
    }
}
