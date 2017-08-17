package com.library.gotopage.base;

/**
 * Author:jmtian
 * Date: 2017/8/14 17:15
 * Commemt:
 * 封装前置条件默认实现，子类实现对应的生命周期函数
 * 控制条件类封装了
 * 1. 验证前该做什么
 * 2. 如何验证状态
 * 3. 验证完成后该做什么
 */

public class BasePreAuthCondition {
    protected BaseContext baseContext;

    public BasePreAuthCondition(BaseContext baseContext) {
        this.baseContext = baseContext;
    }

    /**
     * Author:jmtian
     * Date: 2017/8/14 17:18
     * description: 初始验证
     */

    public void start() {
        int state = getState();
        switch (state) {
            case 0:
                baseContext.setSuccessBeforeIndex();
                successBefore();
                break;
            case 1:
                baseContext.setFailBeforeIndex();
                failBefore();
                break;
            case 2:
                pendingBefore();
                break;
        }
    }

    /**
     * Author:jmtian
     * Date: 2017/8/14 17:18
     * description: 验证完成回调
     */

    public void after() {
        int state = getState();
        switch (state) {
            case 0:
                baseContext.setSuccessAfterIndex();
                successAfter();
                break;
            case 1:
                baseContext.setFailAfterIndex();
                failAfter();
                break;
            case 2:
                pendingAfter();
                break;
        }
    }

    /**
     * 一个前置条件的验证结果一般有下面几种情况
     * 0 通过
     * 1 失败
     * 2 pending
     */
    public int getState() {
        return 0;
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
}
