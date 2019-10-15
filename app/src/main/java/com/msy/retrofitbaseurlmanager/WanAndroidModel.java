package com.msy.retrofitbaseurlmanager;


import com.msy.retrofitbaseurlmanager.base.BaseOutPut;
import com.msy.retrofitbaseurlmanager.base.ServiceCenter;
import com.msy.retrofitbaseurlmanager.bean.HotKeyResponse;
import com.msy.xretrofit.XRetrofit;

import java.util.ArrayList;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;

public class WanAndroidModel {
    public Flowable<BaseOutPut<ArrayList<HotKeyResponse>>> getHotKeys() {
        return ServiceCenter.getWanAndroidService().getHotKey()
                .compose(XRetrofit.<BaseOutPut<ArrayList<HotKeyResponse>>>getScheduler());
    }
}
