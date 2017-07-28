package com.library.gotopage.gotopage;

import android.content.Context;
import android.content.Intent;

/**
 * Name:GotoPageInterface
 * Author:jmtian
 * Commemt:gotopage相关打断条件接口
 * Date: 2017/7/25 14:27
 */


public interface GotoPageInterface {
    //item相关数据回调
    boolean onGotoPageConditionBackListener(NavigationItemVO itemVO, Context fromActivity);
    //类名回调
    Intent onGotoPageGetActivityClassListener(Class clazz, Intent intent);
    //获取对应属性
    String onGotoPageGetUserPropertyByName(String name);
}
