package com.ljx.common.constant;

/**
 * @Author: LiJunXi
 * @Description:
 * @Date: 2025-08-02  20:46
 */
public class CacheConstant {
    public static final String CATEGORY_LIST = "category_list";
    public static final Long CATEGORY_LIST_EXPIRE = 3L;
    public static final String CATEGORY_LOCK = "category_lock";
    public static final Long CATEGORY_LOCK_EXPIRE = 3L;


    public static final String CATEGORY_LOCK_RETRY = "category_lock_retry:";
    public static final Long CATEGORY_LOCK_COUNT_EXPIRE = 1L;

    public static final String SMS_PHONE = "sms_phone:";
    public static final Long SMS_PHONE_EXPIRE = 10L;


    public static final Integer CATEGORY_LOCK_MAX_RETRY = 3;
    public static final Integer CATEGORY_LOCK_DEFAULT_RETRY = 0;
}
