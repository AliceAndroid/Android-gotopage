package com.library.gotopage.base;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.library.gotopage.data.ConditionEnum;

import java.util.HashMap;
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
    public static final int STATE_DEFAULT = -1;//未验证
    public static final int STATE_SUCCESS = 0;//成功
    public static final int STATE_FAIL = 1;//失败
    public static final int STATE_PENDING = 2;//成功

    /**
     * 当前在执行的前置条件下标
     */
    private int currentConditionIndex = -1;

    /**
     * 权限状态记录
     */
    protected Map<Integer, ConditionEnum> stateMap = new HashMap<>();

    protected Activity context;
    protected BaseConfigVO baseConfigVO;
    protected Map<String, String> params;//跳转需要的参数
    protected int flags;


    public int getCurrentConditionIndex() {
        return currentConditionIndex;
    }

    public Activity getContext() {
        return context;
    }

    public void setContext(Activity context) {
        this.context = context;
    }

    public BaseConfigVO getBaseConfigVO() {
        return baseConfigVO;
    }

    public void setBaseConfigVO(BaseConfigVO baseConfigVO) {
        this.baseConfigVO = baseConfigVO;
    }

    public Map<String, String> getParams() {
        return baseConfigVO.getResult().getParams();
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

    public BaseContext(Activity context, BaseConfigVO baseConfigVO, int flags) {
        this.context = context;
        this.baseConfigVO = baseConfigVO;
        this.flags = flags;
        this.params = getParams();
    }

    /**
     * 获取权限运行状态集合
     *
     * @return
     */
    public Map<Integer, ConditionEnum> getStateMap() {
        return stateMap;
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
        for (Integer key : stateMap.keySet()) {
            if (ConditionEnum.SUCCESS_AFTER.equals(stateMap.get(key)) ||
                    ConditionEnum.FAIL_AFTER.equals(stateMap.get(key)) ||
                    ConditionEnum.PENDING_AFTER.equals(stateMap.get(key))) {
                return false;
            }
        }
        return true;
    }

    /**
     * 第一个走SuccessBefore 生命周期的权限
     *
     * @return
     */
    public boolean isFirstSuccessBeforeAuthCondition() {
        int index = -1;
        for (Integer key : stateMap.keySet()) {
            if (ConditionEnum.SUCCESS_BEFORE.equals(stateMap.get(key))) {
                index = key;
                break;
            }
        }
        if (index == currentConditionIndex) {
            return true;
        }
        return false;
    }

    /**
     * 记录每一个条件验证的状态
     *
     * @param conditionEnum
     */
    public void setStateSign(ConditionEnum conditionEnum) {

        stateMap.put(currentConditionIndex, conditionEnum);
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

    /**
     * 获取当前在执行的权限
     *
     * @return
     */
    public BasePreAuthCondition getCurrentCondition() {
        return this.getAuthConditionByIndex(currentConditionIndex);
    }

    /**
     * 获取指定下标验证条件的状态
     *
     * @param index
     * @return
     */
    public ConditionEnum getStateAuthByIndex(int index) {
        if (stateMap.containsKey(index)) {
            return stateMap.get(index);
        }
        return ConditionEnum.DEFAULT;
    }

    /**
     * 获取某个验证条件的验证状态
     *
     * @param condition
     * @return
     */
    public ConditionEnum getStateAuth(BasePreAuthCondition condition) {
        int key = getAuthConditionIndex(condition);
        if (stateMap.containsKey(key)) {
            return stateMap.get(key);
        }
        return ConditionEnum.DEFAULT;
    }

    /**
     * 获取验证成功的个数
     *
     * @return
     */
    public int getCountStateAuthSuccess() {
        int number = 0;
        for (Integer key : stateMap.keySet()) {
            if (ConditionEnum.SUCCESS_BEFORE.equals(stateMap.get(key)) ||
                    ConditionEnum.SUCCESS_AFTER.equals(stateMap.get(key))) {
                number++;
            }
        }
        return number;
    }

    /**
     * 获取验证失败的个数
     *
     * @return
     */
    public int getCountStateAuthFail() {
        int number = 0;
        for (Integer key : stateMap.keySet()) {
            if (ConditionEnum.FAIL_BEFORE.equals(stateMap.get(key)) ||
                    ConditionEnum.FAIL_AFTER.equals(stateMap.get(key))) {
                number++;
            }
        }
        return number;
    }

    /**
     * 获取验证进行中的个数
     *
     * @return
     */
    public int getCountStateAuthPending() {
        int number = 0;
        for (Integer key : stateMap.keySet()) {
            if (ConditionEnum.PENDING_BEFORE.equals(stateMap.get(key)) ||
                    ConditionEnum.PENDING_AFTER.equals(stateMap.get(key))) {
                number++;
            }
        }
        return number;
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
    protected Intent getIntent() {
        Class activityClass = getClazz();
        if(null == activityClass){
            return null;
        }
        Intent intent = new Intent();
        if (getParams() != null && !getParams().isEmpty()) {
            for (String key : getParams().keySet()) {
                intent.putExtra(key, getParams().get(key));
            }
        }
        intent.setClass(context, activityClass);
        return intent;
    }

    protected Class getClazz(){
        Class activityClass = null;
        try {
            activityClass = Class.forName(baseConfigVO.getResult().getActivity());
            return activityClass;
        } catch (ClassNotFoundException e) {
            Log.e("NavigationManager", "获取activity失败");
            return null;
        }
    }

}
