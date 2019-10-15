package com.msy.retrofitbaseurlmanager.base;

import com.google.gson.Gson;
import com.msy.retrofitbaseurlmanager.GithubService;
import com.msy.retrofitbaseurlmanager.WanAndroidService;
import com.msy.xretrofit.XRetrofit;

import java.util.Map;

import okhttp3.MediaType;
import okhttp3.RequestBody;


public class ServiceCenter {
    public static RequestBody generateJsonRequestBody(Map map) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), new Gson().toJson(map));
    }


    public static RequestBody generateJsonRequestBody(String json) {
        return RequestBody.create(MediaType.parse("application/json; charset=utf-8"), json);
    }

    private static GithubService sGithubService;

    public static GithubService getGithubService() {
        if (sGithubService == null) {
            synchronized (ServiceCenter.class) {
                if (sGithubService == null) {
                    sGithubService = XRetrofit.getInstance().getRetrofit(BaseUrlConfig.baseUrl_GitHub, true).create(GithubService.class);
                }
            }
        }
        return sGithubService;
    }

    private static WanAndroidService sWanAndroidService;

    public static WanAndroidService getWanAndroidService() {
        if (sWanAndroidService == null) {
            synchronized (ServiceCenter.class) {
                if (sWanAndroidService == null) {
                    sWanAndroidService = XRetrofit.getInstance().getRetrofit(BaseUrlConfig.baseUrl_WanAndroid, true).create(WanAndroidService.class);
                }
            }
        }
        return sWanAndroidService;
    }
}


