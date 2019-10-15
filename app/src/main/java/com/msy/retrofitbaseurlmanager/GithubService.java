package com.msy.retrofitbaseurlmanager;

import io.reactivex.Flowable;
import okhttp3.ResponseBody;
import retrofit2.http.GET;
import retrofit2.http.Path;

//https://api.github.com/users/chenxy
public interface GithubService {

    @GET("users/{userName}")
    Flowable<ResponseBody> getUserInfo(@Path("userName") String userName);

}
