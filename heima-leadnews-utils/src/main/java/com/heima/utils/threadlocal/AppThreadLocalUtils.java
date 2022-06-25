package com.heima.utils.threadlocal;

import com.heima.model.user.pojos.ApUser;
import com.heima.model.wemedia.pojos.WmUser;

public class AppThreadLocalUtils {

    private final static ThreadLocal<ApUser> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户
     * @param wmUser
     */
    public static void setUser(ApUser wmUser){
        USER_THREAD_LOCAL.set(wmUser);
    }

    /**
     * 获取当前线程中的用户
     * @return
     */
    public static ApUser getUser(){
        return USER_THREAD_LOCAL.get();
    }
}
