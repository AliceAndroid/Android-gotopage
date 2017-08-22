package com.library.gotopage.base;

import com.library.gotopage.data.ConditionEnum;
import com.library.gotopage.data.StateInterface;
import com.library.gotopage.manager.NavigationManager;

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
        checkoutState(this);
    }

    /**
     * Author:jmtian
     * Date: 2017/8/14 17:18
     * description: 验证完成回调
     */

    public void after() {
        isStart = false;
        checkoutState(this);
    }

    /**
     * 一个前置条件的验证
     * 0 通过
     * 1 失败
     * 2 pending
     */
    public void checkoutState(StateInterface stateInterface) {

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
        NavigationManager.getInstance().release();
    }

    @Override
    public void stateCallBack(int state) {
        switch (state) {
            case BaseContext.STATE_SUCCESS:
                if (isStart) {
                    baseContext.setStateSign(ConditionEnum.SUCCESS_BEFORE);
                    successBefore();
                } else {
                    baseContext.setStateSign(ConditionEnum.SUCCESS_AFTER);
                    successAfter();
                }
                break;
            case BaseContext.STATE_FAIL:
                if (isStart) {
                    baseContext.setStateSign(ConditionEnum.FAIL_BEFORE);
                    failBefore();
                } else {
                    baseContext.setStateSign(ConditionEnum.FAIL_AFTER);
                    failAfter();
                }
                break;
            case BaseContext.STATE_PENDING:
                if (isStart) {
                    baseContext.setStateSign(ConditionEnum.PENDING_BEFORE);
                    pendingBefore();
                } else {
                    baseContext.setStateSign(ConditionEnum.PENDING_AFTER);
                    pendingAfter();
                }
                break;
        }
    }
}
