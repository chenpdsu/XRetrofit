package com.msy.retrofitbaseurlmanager;

import com.msy.retrofitbaseurlmanager.base.BaseOutPut;
import com.msy.retrofitbaseurlmanager.bean.HotKeyResponse;

import java.util.ArrayList;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

//https://www.wanandroid.com/hotkey/json
public interface WanAndroidService {

    @GET("hotkey/json")
    Flowable<BaseOutPut<ArrayList<HotKeyResponse>>> getHotKey();

}
