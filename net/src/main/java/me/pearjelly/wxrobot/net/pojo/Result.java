package me.pearjelly.wxrobot.net.pojo;

import java.lang.reflect.Array;

/**
 * Created by hxb on 2016/5/1.
 */
public class Result {
    String status;
    DeviceInfo[] data;

    public Result() {

    }

    @Override
    public String toString() {
        return "Result{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
