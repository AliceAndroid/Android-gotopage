package com.library.gotopage.data;

/**
 * Name:ConditionEnum
 * Author:jmtian
 * Commemt:condition各种状态记录
 * Date: 2017/8/21 14:17
 */


public enum ConditionEnum {
    DEFAULT,
    SUCCESS_BEFORE,
    PENDING_BEFORE,
    FAIL_BEFORE,
    SUCCESS_AFTER,
    PENDING_AFTER,
    FAIL_AFTER;

    ConditionEnum() {

    }
}
