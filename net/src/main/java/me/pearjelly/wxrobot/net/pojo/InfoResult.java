package me.pearjelly.wxrobot.net.pojo;

/**
 * Created by hxb on 2016/5/9.
 */
public class InfoResult {
    public String status;
    public DeviceInfo data;

    @Override
    public String toString() {
        return "InfoResult{" +
                "status='" + status + '\'' +
                ", data=" + data +
                '}';
    }
}
