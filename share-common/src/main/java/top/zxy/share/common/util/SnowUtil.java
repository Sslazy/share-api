package top.zxy.share.common.util;

import cn.hutool.core.util.IdUtil;

import java.util.Date;

public class SnowUtil {
    private static final long DATA_CENTER_ID = 1;

    private static final long WORKER_ID = 1;

    public static long getSnowflakeNextID(){
        return IdUtil.getSnowflake(WORKER_ID,DATA_CENTER_ID).nextId();
    }

    public static String getSnowflakeNextIdStr(){
        return IdUtil.getSnowflake(WORKER_ID,DATA_CENTER_ID).nextIdStr();
    }
}
