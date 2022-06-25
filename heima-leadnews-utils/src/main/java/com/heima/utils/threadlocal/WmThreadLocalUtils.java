package com.heima.utils.threadlocal;

import com.heima.model.wemedia.pojos.WmUser;

public class WmThreadLocalUtils {
    private final static ThreadLocal<WmUser> USER_THREAD_LOCAL = new ThreadLocal<>();

    /**
     * 设置当前线程中的用户
     * @param wmUser
     */
    public static void setUser(WmUser wmUser){
        USER_THREAD_LOCAL.set(wmUser);
    }

    /**
     * 获取当前线程中的用户
     * @return
     */
    public static WmUser getUser(){
        return USER_THREAD_LOCAL.get();
    }
}
