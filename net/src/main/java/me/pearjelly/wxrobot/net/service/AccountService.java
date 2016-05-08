package me.pearjelly.wxrobot.net.service;

import me.pearjelly.wxrobot.net.pojo.GenPasswdResult;
import me.pearjelly.wxrobot.net.pojo.UploadResult;
import retrofit2.Call;
import retrofit2.http.Field;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.Path;

/**
 * Created by hxb on 2016/5/7.
 */
public interface AccountService {
    @GET("deviceinfo/genWxPasswd/{phonenumber}")
    Call<GenPasswdResult> genPasswd(@Path("phonenumber") String phonenumber);

    @FormUrlEncoded
    @POST("deviceinfo/uploadPhonenumber")
    Call<UploadResult> uploadPhonenumber(@Field("phonenumber") String phonenumber
            , @Field("imsi") String imsi, @Field("wxpasswd") String wxpasswd);
}
