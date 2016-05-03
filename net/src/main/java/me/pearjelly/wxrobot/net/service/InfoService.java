package me.pearjelly.wxrobot.net.service;

import me.pearjelly.wxrobot.net.pojo.DeviceInfo;
import me.pearjelly.wxrobot.net.pojo.Result;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by hxb on 2016/5/1.
 */
public interface InfoService {
    @POST("deviceinfo/uploadInfo")
    Call<Result> createInfo(@Body DeviceInfo deviceInfo);
}
