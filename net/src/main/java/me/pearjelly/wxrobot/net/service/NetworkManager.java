package me.pearjelly.wxrobot.net.service;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by hxb on 2016/5/1.
 */
public class NetworkManager {
    private static NetworkManager ourInstance = new NetworkManager();
    private final Retrofit retrofit;

    public static NetworkManager getInstance() {
        return ourInstance;
    }

    private NetworkManager() {
        retrofit = new Retrofit.Builder()
        .baseUrl("http://snowberg.wmzzd.com/")

                .addConverterFactory(GsonConverterFactory.create())
                .build();
    }

    public InfoService getInfoService() {
        InfoService service = retrofit.create(InfoService.class);
        return service;
    }

    public AccountService getAccountService() {
        AccountService service = retrofit.create(AccountService.class);
        return service;
    }
}
