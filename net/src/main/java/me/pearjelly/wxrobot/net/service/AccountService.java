package me.pearjelly.wxrobot.net.service;

import me.pearjelly.wxrobot.net.pojo.GenPasswdResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;

/**
 * Created by hxb on 2016/5/7.
 */
public interface AccountService {
    @GET("deviceinfo/genWxPasswd/{phonenumber}")
    Call<GenPasswdResult> genPasswd(@Path("phonenumber") String phonenumber);
}
