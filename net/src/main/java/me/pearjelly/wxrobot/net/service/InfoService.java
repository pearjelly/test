package me.pearjelly.wxrobot.net.service;

import me.pearjelly.wxrobot.net.pojo.DeviceInfo;
import me.pearjelly.wxrobot.net.pojo.Result;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.POST;

/**
 * Created by hxb on 2016/5/1.
 */
public interface InfoService {
    @POST("deviceinfo/uploadInfo")
    Call<Result> createInfo(@Body DeviceInfo deviceInfo);

    @FormUrlEncoded
    @POST("deviceinfo/uploadInfo")
    Call<Result> createInfo(@Field("serial") String serial, @Field("imei") String imei
            , @Field("wifimac") String wifimac, @Field("bluemac") String bluemac
            , @Field("androidid") String androidid, @Field("brand") String brand
            , @Field("manufacturer") String manufacturer, @Field("model") String model
            , @Field("netcountryiso") String netcountryiso
            , @Field("simcountryiso") String simcountryiso
            , @Field("phonenumber") String phonenumber
            , @Field("imsi") String imsi, @Field("simserial") String simserial
            , @Field("wxpasswd") String wxpasswd);
}
