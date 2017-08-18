package com.library.gotopage.base;

import com.library.gotopage.data.StateInterface;

/**
 * Author:jmtian
 * Date: 2017/8/14 17:15
 * Commemt:
 * 封装前置条件默认实现，子类实现对应的生命周期函数
 * 控制条件类封装包括：
 * 验证前判断
 * 验证状态
 * 验证后实现
 */

public class BasePreAuthCondition implements StateInterface {

    protected BaseContext baseContext;
    private boolean isStart = false;//是否是start方法

    public BasePreAuthCondition(BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    /**
     * Author:jmtian
     * Date: 2017/8/14 17:18
     * description: 初始验证
     */

    public void start() {
        isStart = true;
        getState(this);
    }

    /**
     * Author:jmtian
     * Date: 2017/8/14 17:18
     * description: 验证完成回调
     */

    public void after() {
        isStart = false;
        getState(this);
    }

    /**
     * 一个前置条件的验证结果一般有下面几种情况
     * 0 通过
     * 1 失败
     * 2 pending
     */
    public void getState(StateInterface stateInterface) {

    }


    public void successBefore() {
        baseContext.next();
    }

    public void pendingBefore() {

    }

    public void failBefore() {
    }

    public void successAfter() {
        baseContext.next();
    }

    public void pendingAfter() {

    }

    public void failAfter() {

    }

    @Override
    public void stateCallBack(int state) {
        baseContext.setStateSign(state);
        switch (state) {
            case BaseContext.STATE_SUCCESS:
                if (isStart) {
                    baseContext.setSuccessBeforeIndex();
                    successBefore();
                } else {
                    baseContext.setSuccessAfterIndex();
                    successAfter();
                }
                break;
            case BaseContext.STATE_FAIL:
                if (isStart) {
                    baseContext.setFailBeforeIndex();
                    failBefore();
                } else {
                    baseContext.setFailAfterIndex();
                    failAfter();
                }
                break;
            case BaseContext.STATE_PENDING:
                if (isStart) {
                    baseContext.setPendingBeforeIndex();
                    pendingBefore();
                } else {
                    baseContext.setPendingAfterIndex();
                    pendingAfter();
                }
                break;
        }
    }
}
