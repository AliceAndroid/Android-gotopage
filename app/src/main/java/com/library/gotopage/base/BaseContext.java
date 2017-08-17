package com.library.gotopage.base;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import java.util.Map;

/**
 * Author:jmtian
 * Date: 2017/8/14 17:10
 * description:
 * 某个跳转的上下文对象
 * 包含了跳转需要的初始信息，包括但不限于发起跳转的源、跳转配置、跳转参数
 * 管理执行跳转过程中的运行时信息，包括但不限于前置条件的执行情况
 */


public class BaseContext {
    /**
     * 当前在执行的前置条件
     */
    private int currentConditionIndex = -1;
    /**
     * 当前第几个执行successBefore生命周期短
     */
    private int successBeforeIndex = -1;
    /**
     * 当前第几个执行failBefore生命周期短
     */
    private int failBeforeIndex = -1;

    /**
     * 当前第几个执行successAfter生命周期短
     */
    private int successAfterIndex = -1;
    /**
     * 当前第几个执行failAfter生命周期短
     */
    private int failAfterIndex = -1;

    private Context context;
    private BaseConfigVO baseConfigVO;
    private Map<String, String> params;//跳转需要的参数
    private int flags;


    public void setSuccessAfterIndex() {
        this.successAfterIndex++;
    }

    public void setSuccessBeforeIndex() {
        this.successBeforeIndex++;
    }

    public void setFailBeforeIndex() {
        this.failBeforeIndex++;
    }

    public void setFailAfterIndex() {
        this.failAfterIndex++;
    }

    public int getSuccessBeforeIndex() {
        return successBeforeIndex;
    }

    public int getFailBeforeIndex() {
        return failBeforeIndex;
    }

    public int getSuccessAfterIndex() {
        return successAfterIndex;
    }

    public int getFailAfterIndex() {
        return failAfterIndex;
    }

    public Context getContext() {
        return context;
    }

    public void setContext(Context context) {
        this.context = context;
    }

    public BaseConfigVO getBaseConfigVO() {
        return baseConfigVO;
    }

    public void setBaseConfigVO(BaseConfigVO baseConfigVO) {
        this.baseConfigVO = baseConfigVO;
    }

    public Map<String, String> getParams() {
        return params;
    }

    public void setParams(Map<String, String> params) {
        this.params = params;
    }

    public int getFlags() {
        return flags;
    }

    public void setFlags(int flags) {
        this.flags = flags;
    }

    public BaseContext(Context context, BaseConfigVO baseConfigVO, int flags) {
        this.context = context;
        this.baseConfigVO = baseConfigVO;
        this.params = baseConfigVO.getParams();
        this.flags = flags;
    }

    /**
     * 是否是权限组最后一个
     *
     * @return
     */
    public boolean isLastAuthCondition(BasePreAuthCondition condition) {
        return (getAuthConditionSize() - 1) == getAuthConditionIndex(condition);
    }

    /**
     * 是否是权限组第一个数据
     *
     * @return
     */
    public boolean isFirstAuthCondition(BasePreAuthCondition condition) {
        return 0 == getAuthConditionIndex(condition);
    }

    /**
     * 第一个走FailBefore 生命周期的权限
     *
     * @return
     */
    public boolean isFirstFailBeforeAuthCondition() {
        return 0 == getFailBeforeIndex();
    }

    /**
     * 第一个走SuccessBefore 生命周期的权限
     *
     * @return
     */
    public boolean isFirstSuccessBeforeAuthCondition() {
        return 0 == getSuccessBeforeIndex();
    }


    /**
     * 根据权限验证类获取对应下标
     *
     * @param condition
     * @return
     */
    public int getAuthConditionIndex(BasePreAuthCondition condition) {
        return baseConfigVO.getAuthConditionIndex(condition);
    }

    /**
     * 根据下标获取权限验证类
     *
     * @param index
     * @return
     */
    public BasePreAuthCondition getAuthConditionByIndex(int index) {
        return baseConfigVO.getAuthConditionByIndex(this, index);
    }

    /**
     * 获取当前验证条件的配置参数
     *
     * @return
     */
    public Map<String, String> getAuthConditionParams(BasePreAuthCondition condition) {
        return baseConfigVO.getAuthConditionParams(getAuthConditionIndex(condition));
    }

    /**
     * 获取权限组的数量
     *
     * @return
     */
    public int getAuthConditionSize() {
        return baseConfigVO.getAuthConditionSize();
    }

    /**
     * 检查当前条件以及未验证条件，返回是否还有未验证的前置条件
     */
    public boolean hasUnVerifyCondition() {
        return !baseConfigVO.getAuthGroup().isEmpty() &&
                currentConditionIndex != (getAuthConditionSize() - 1);
    }

    public BasePreAuthCondition getCurrentCondition() {
        return this.getAuthConditionByIndex(currentConditionIndex);
    }

    /**
     * 执行下一个前置条件
     */
    public void next() {
        if (hasUnVerifyCondition()) {
            BasePreAuthCondition nextCondition = baseConfigVO.getAuthConditionByIndex(this, ++currentConditionIndex);
            nextCondition.start();
        } else {
            gotoTarget();
        }
    }

    /**
     * 直接跳转到目标页面
     * 默认实现是创建新的控制器对象并直接跳转
     * 如果需要可以直接跳转
     */
    public void gotoTarget() {
        Intent intent = getIntent();
        if (null == intent) {
            return;
        }
        if (flags != -1) {
            intent.setFlags(flags);
        }
        context.startActivity(intent);
    }

    /**
     * 获取Intent对象
     *
     * @return
     */
    private Intent getIntent() {
        Class activityClass = null;
        try {
            activityClass = Class.forName(baseConfigVO.getActivityName());
        } catch (ClassNotFoundException e) {
            Log.e("NavigationManager", "获取activity失败");
            return null;
        }
        Intent intent = new Intent();
        if (params != null && !params.isEmpty()) {
            for (String key : params.keySet()) {
                intent.putExtra(key, params.get(key));
            }
        }
        intent.setClass(context, activityClass);
        return intent;
    }

}
