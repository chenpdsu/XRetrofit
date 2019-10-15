package com.msy.retrofitbaseurlmanager;


import com.msy.retrofitbaseurlmanager.base.ServiceCenter;
import com.msy.xretrofit.XRetrofit;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;

public class GitHubModel {
    public Flowable<ResponseBody> getEthBalance(String userName) {
        return ServiceCenter.getGithubService().getUserInfo(userName)
                .compose(XRetrofit.<ResponseBody>getScheduler());
    }
}
